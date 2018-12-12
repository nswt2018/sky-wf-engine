/*
 * @(#)WorkflowFilters.java
 * 
 * Beijing Beida Jade Bird Business Information System Co.,Ltd
 *
 * Copyright (c) 2009 JBBIS. All Rights Reserved.
 *
 * http://www.jbbis.com.cn
 */

package cn.com.jbbis.jbportal.workflow;

import java.util.ArrayDeque;
import java.util.Queue;

import cn.com.jbbis.afx.AppContext;
import cn.com.jbbis.afx.AppFilter;
import cn.com.jbbis.afx.AppFilterChain;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.Application;
import cn.com.jbbis.afx.ServiceOK;
import cn.com.jbbis.common.LoanApp;
import cn.com.jbbis.common.LoanServiceContext;
import cn.com.jbbis.common.ehcahe.EhcacheTools;
import cn.com.jbbis.common.util.JBDate;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.util.Strings;
import cn.com.jbbis.util.UnikMap;


/**
 * ���������
 * <P>
 * �ж��Ƿ�������,�Լ����ĸ����̣���ִ��ÿ�����׵�ʱ�򶼵���ִ��doFilter����,��Filter����Ӧ����ֻ��һ������.
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-7 ����05:09:56
 * @since   JBPortal3.0-njloan
 */

public class WorkflowFilter implements AppFilter{
	/**
	 * LoanApp��Ҫ�����õ�Ӧ�õ�һЩ����
	 */
	private LoanApp app;
	
	public void init(Application app) throws Exception {
		this.app = (LoanApp) app;
		this.app.log(Service.INFO, "Starting WorkflowFilter ...");		
	}

	public void destroy() {
		app.log(Service.INFO, "destory WorkflowFilter OK");
	}
	
	private String fmt(String s){
		return " [ " + s + " ] ";
	}

	/**
	 * ����������֮ǰִ�д˷�����
	 * <ol>
	 * <li>�ж��Ƿ���Ҫ������������,ÿ�����׽��й���,svc��������wfisrun=false���ߵ�������;
	 * �����Ҫ�ߵ�������,���ڽ��ױ�������Ҫ����wfid��������ʵ�����,������ֵΪ����ᱻ��Ϊ
	 * ���̵Ŀ�ʼ�ڵ�,ʲô�ύ������ʲô���̶���ҪӦ��ȥ����"����������Χ"</li>
	 * <li>�����һ�ڵ������������ֹ�����Ļ����ֹ�ѡ���Ĵ�����Ա��Ҫ�����LoanServiceContext
	 * �У�lsc.setProperties("users",�û�);�������������潫�޷�ȡ��Ӧ���ֹ�ѡ�����Ա��Ϣ</li>
	 * <li>ÿ���ڵ�Ĵ������Ҳ��Ӧ�ô����LoanServiceContext������,�������·���ж�ʱû���ж�
	 * �����ݣ���ŷ�ʽlsc.setProperties("workflow",���������Ϣ)��
	 * �û��ʹ�����˼����doFilter��ȡ�����������������</li>
	 * 
	 * ���wfisrun==true && wfselwin==false����request�еı�������Session����סԭʼ����Tid001
	 * ��λ��ѡ�˺�����Ľ���Tid002
	 * ���peoplepopion==true�����������������ύ
	 * ִ��ԭʼ����Tid001
	 * </ol>
	 */
	public AppResponse doFilter(AppContext arg0, AppFilterChain arg1)
			throws Exception {
		
		// ������׵�.svc������wfisrun=false,��˽��ײ�������������
		String isStartWorkflow = arg0.getString("wfisrun");		
		String isWfSelWin = arg0.getString("wfselwin");
		String wfDefId = "";
		String startCreateWfInstId = "";  //���̸ս����ʱ�򣬻�û�д�������ʵ����ţ�wfid������Ҫ�Ӵ������ַ�����
		UnikMap umWfid = new UnikMap();
		AppRequest request = arg0.getRequest();           //��������Ϣ
		String tranCode = request.getHeader("tranCode");  //��̨�����׺�		
		//���׶���
		Queue<UnikMap> tranQueue = new ArrayDeque<UnikMap>();				
		//arg0.log(Service.INFO, "[WF_doFilter] wfisrun = " + isStartWorkflow + fmt(tranCode));		
		if ("true".equalsIgnoreCase(isStartWorkflow) ) {
//		if ("true".equalsIgnoreCase(isStartWorkflow) || "true".equalsIgnoreCase(isWfSelWin)) {	
//			//��һ���ύ��isWfSelWin=null���ʶ���ѡ�˽��棻ȷ����isWfSelWin=true
//			if(!"true".equalsIgnoreCase(isWfSelWin)) {
////				request.setHeader("tranCode", CommonConst.WF_SEL_WIN);
////				request.setHeader("verb", "IN-EXEC");
////				request.setHeader("tid", CommonConst.WF_SEL_WIN);
////				return arg1.doFilter(arg0);
//				arg0.getAppContext()
//			}
			String wfInstId = request.getString("wfid");  //��ȡ�ϴ������еĹ��������к�,  ע�⣺���ν����ʱ�򣬴���Ĳ���wfid����flowid
			wfDefId = wfInstId;
			// ������������Ϊ��,����Ҫ�жϵ�ǰ�����Ƿ���Ҫ��������,�������̱�Ų�Ϊ��,˵���ҵ��˵�������������ڣ�����Ҫ�����µ�����
			if (wfInstId == null || wfInstId.equals("")) {
				wfInstId = new CheckWorkflow(arg0).checkStartCondition(tranCode);
				if (wfInstId != null) {
					wfInstId = "start:" + wfInstId;
					request.setField("workflowisrun", "true"); //��Ҫ��Ӧ�����ж��Ƿ�������ʹ��,��ʵӦ�ÿ��Ը���wfid���ж�
				} else
					request.setField("workflowisrun", "false");
			}else
				request.setField("workflowisrun", "true");
			request.setHeader("wf_workflowid", wfInstId);
		}
		
		//AppResponse res = arg1.doFilter(arg0);            // ����Ӧ���еĽ���(�ڵ������õĽ��ף������ִ�С��ύ��)
				
		AppResponse dores = null;
		if (isStartWorkflow!=null && "true".equalsIgnoreCase(isStartWorkflow)){
		  dores = doFilterAfter(arg0,umWfid,tranQueue);		 
		}
		//arg0.log(Service.INFO, "[WF_doFilter] startCreateWfInstId = " + umWfid.getString("wfInstId") );
		startCreateWfInstId = (String)umWfid.getString("wfInstId");
		//�ó��ε��滻
		if(startCreateWfInstId!=null && !startCreateWfInstId.equals("")){
			wfDefId = startCreateWfInstId;
		}
		if(wfDefId==null || wfDefId.equals("")){
			wfDefId = request.getHeader("wf_workflowid");
			if (wfDefId!=null && wfDefId.startsWith("start:")){							
				//��ȡ���� ���
				wfDefId = wfDefId.substring(6);
			}
		}
					
		//����������Ϣ��Session�У������ύ����(��һ���ڵ���Ϣ���û���Ϣ)
		if(arg0.getUser()!=null && arg0.getUser().getSession()!=null){
		   arg0.getUser().getSession().setAttribute("output_wfid", wfDefId);
		}			
		AppResponse res = arg1.doFilter(arg0);            // ����Ӧ���еĽ���(�ڵ������õĽ��ף������ִ�С��ύ��)
		//�Ӷ����л�ȡ����,��ִ��
		while(!tranQueue.isEmpty()){
			UnikMap tranUm = tranQueue.poll();
			UnikMap node = (UnikMap)tranUm.get("trancode");
			UnikMap var = (UnikMap)tranUm.get("vars");
			String trantype = tranUm.getString("trantype");			
			if(trantype!=null && trantype.equals("endtrans")){
				String tCode = node.getString("submtrancode");
				if(tCode!=null && !tCode.equals("")){
					res = doEndService(arg0,node,var);	
					if (res instanceof ServiceOK) {
						// �������ͨ�Ľ��׳ɹ���ʾ,�򽫴���Ϣ�����޸�
						//LoanServiceContext lsc = (LoanServiceContext)arg0;
						WorkflowStorageImpl wfsi = new WorkflowStorageImpl(arg0);
						
						//modify by mahong 20150326
						String fid = request.getString("flowid");
						if(!Strings.isEmpty(fid)) {
							UnikMap wfdef = wfsi.getWorkflowDefinition(fid);
							res = new ServiceOK(wfsi.formatMessage("WF_OutWorkflowAndSucceeded", wfdef.getString("flowname")));
						} else {
							res = new ServiceOK(wfsi.formatMessage("WF_OutWorkflowAndSucceeded",var.getString("flowname")));
						}
					}	
				}				
			}
			else if(trantype!=null && trantype.equals("retutrans")){  //�˻ؽ���				
					String tCode = node.getString("retutrancode");
					if(tCode!=null && !tCode.equals("")){
						res = doReturnService(arg0,node,var);
					}					
				}
		}		
		//����ڵ���Ϣ
		return dores == null ? res : dores;
		//return dores;
	}
	/**
	 * ִ���˻ؽ���
	 * @param ctx     ������
	 * @param node    ִ��ʱ�ĵ�ǰ�ڵ�
	 * @param varsUm  ��¼�ĵ�ʱ�����̱���
	 * @return
	 * @throws Exception
	 */
	private AppResponse doReturnService(AppContext ctx,UnikMap node,UnikMap varsUm) throws Exception
	{
		LoanServiceContext lsc = (LoanServiceContext)ctx;
		WorkflowStorageImpl wfsi = new WorkflowStorageImpl(ctx);
		Workflow wf = new Workflow(wfsi);
		return wfsi.doReturnService(node,varsUm);
	}
	/**
	 * ���̽���ִ�н�������
	 * @param ctx
	 * @param node    �������׵Ľڵ�
	 * @param varsUm  ��¼�ĵ�ʱ�����̱���
	 * @return
	 * @throws Exception
	 */
	private AppResponse doEndService(AppContext ctx,UnikMap node,UnikMap varsUm) throws Exception
	{
		LoanServiceContext lsc = (LoanServiceContext)ctx;
		WorkflowStorageImpl wfsi = new WorkflowStorageImpl(ctx);
		Workflow wf = new Workflow(wfsi);
		return wfsi.doEndService(node,varsUm);
	}
	/**
	 * ��������ִ��֮��ִ�д˷�����
	 * <ol>
	 * <li>�жϴ˽����Ƿ�Ҫ��Ҫ�߹�������,��Ҫ�Ǵ�request��ȡwf_workflowidֵ��</li>
	 * <li>�����Ҫ�߹������̣����ж��������µ����̻���ֱ�ӽ�����������,����wfid���жϡ�</li>
	 * <li>������߹������̣�����ϴ����Ķ�����ȡ������������workflow��
	 * ·���ж��������ǹ���������������Ҫ�ı������Ǵ�workflow�������ֵ��ȡ�ã�
	 * ����ϴ�������û��workflow���������ȥ������dwtaskvars��ȡ�µ�ֵ
	 * ��������ǿ�ʼ�ڵ���ȡ��������Ա���ڵ������������Զ��Ļ�������Աȡ���� Ϊnull��ֻ���ֹ�����users�Ż���ֵ</li>
	 * </ol>
	 */
//	public AppResponse doFilterAfter(AppContext ctx , AppResponse res) throws Exception{
	public AppResponse doFilterAfter(AppContext ctx ,UnikMap umWfid,Queue<UnikMap> tranQueue) throws Exception{
		AppRequest request = ctx.getRequest();
		UnikMap outTaskNode = new UnikMap();
		boolean isException = false;
		String exceptionMessage = "";		
		String flowid = request.getHeader("wf_workflowid");
		AppResponse response = null;
		if (flowid != null) {
			String tranCode = request.getHeader("tranCode");
			long b = System.currentTimeMillis();
			LoanServiceContext lsc = (LoanServiceContext)ctx;
			WorkflowStorageImpl wfsi = new WorkflowStorageImpl(ctx);
			Workflow wf = new Workflow(wfsi);		
			
			//========��ȡ����������==========			
			//Object object = lsc.getProperties("workflow");
//			if(object==null){
//				object = ctx.getUser().getSession().getAttribute("workflow");
//			}
//			if(object==null){
//				//ctx.log(Service.DEBUG,"you must set var of workflow" );	
//				isException = true;
//				exceptionMessage = "���̱����Ѷ�ʧ����δ���ã�";
//				//Assert(object!=null,"���������̱�����");
//			}
//			UnikMap wfData = new UnikMap();
//			if (wfData instanceof UnikMap) {
//				wfData.putAll((UnikMap) object);
//			}else
//				//ctx.log(Service.WARN, "workflow data type not UnikMap.");

			/*
			 * ȡ���������������Ƿ�ÿ���ύ��Ҫȡһ�Σ� �ύ�����ϸ�ҵ��������������ֵҪ�ύ��
			 * simon modified on 2017-08-31 
			 */
			Object object = lsc.getProperties("workflow");
			UnikMap wfData = new UnikMap();
			if (object==null) {
				BusiDataLoad dl = new BusiDataLoad(lsc);
				wfData.putAll(dl.getFieldInfo());
			}else{
				wfData.putAll((UnikMap) object);
			}		
			
			

			String user = request.getField("s_bankid") + "/" + request.getField("s_operid");
			//������ȡ��users��Ϣ��������ֹ����䴦����Ա��users��Ӧ��Ϊnull,����û�����Ƿ��Ҫ��
			String users[] = null;
			String bankid = request.getString("s_bankid");
			String userid = request.getString("users");
			if (userid != null)
			{
				users = userid.split(",");
				for (int i = 0; i < users.length; i++) {
					users[i]=bankid+"/"+users[i];
				}
			}

//			Object obj = lsc.getProperties("users");
//			if(obj==null){
//				obj = ctx.getUser().getSession().getAttribute("users");				
//			}
//			if (obj instanceof String[]) {	
//				//ctx.log(Service.DEBUG,"get users from Session " );
//				users = (String[]) obj;
//				for (int i = 0; i < users.length; i++) {
//					System.out.println("�û���Ϣ:" + users[i]);
//					if (users[i].indexOf("/") < 1) {
//						users = null;
//						break;
//					}
//				}
//			}
//			else{
//				//ctx.log(Service.DEBUG,"get users from Session null"  );
//			}
			
			//������ȡ��users��Ϣ��������ֹ����䴦����Ա��users��Ӧ��Ϊnull,����û�����Ƿ��Ҫ��
			String viewusers[] = null;
			Object viewobj = lsc.getProperties("viewusers");
			if(viewobj==null){
				//ctx.log(Service.DEBUG,"set viewusers from Session " );
				viewobj = ctx.getUser().getSession().getAttribute("viewusers");
			}
			if (viewobj instanceof String[]) {				
				//ctx.log(Service.DEBUG,"get viewusers from Session" );
				viewusers = (String[]) viewobj;				
				for (int i = 0; i < viewusers.length; i++) {
					if (viewusers[i].indexOf("/") < 1) {
						viewusers = null;
						break;
					}
				}
			}
			else{
				//ctx.log(Service.DEBUG,"get viewusers from Session null"  );
			}			
			boolean blIsDirectForeNode = false;  //����Ӧ����ȫ�ֱ���	
			
			EhcacheTools cacheTools = new EhcacheTools();
			WFParams params = new WFParams();
			
			params.setBankid(bankid);
			params.setFlowid(request.getField("flowid"));
			//params.setWfid(wfid);
			//params.setNodeid(nodeid);
			params.setOperid(request.getField("operid"));
			params.setOpername(request.getField("opername"));
			params.setTaskser(request.getField("taskser"));
			params.setDealopin(request.getField("dealopin"));
			params.setOtheropin(request.getField("otheropin"));
			params.setInstancylevel(request.getField("instancylevel"));
			System.out.println(params);
			
			
			if (flowid.startsWith("start:")) {
				//ctx.log(Service.DEBUG, "start workflow");				
				//��ȡ����Ĳ���
				UnikMap umWorkflow = (UnikMap)ctx.getUser().getSession().getAttribute("busiworkflow");				
				//��ȡ���� ���
				String wfDefId = flowid.substring(6);
				
				//��ʼ������, umWfid��outTaskNode���ǿյģ�umWorkflow�ⲿ�����ҵ�����
				
				//��ȡ����
				UnikMap dwflowmainVo = cacheTools.queryDwflowmain(wfDefId);
				String flowtemplate = dwflowmainVo.getString("flowtemplate");
				//ҵ������ģ��
				Class c = Class.forName(flowtemplate);
				WorkFlowAdapter wfa = (WorkFlowAdapter)c.newInstance();
				
				//��ʼ��
				wfa.bizInit(params);
				
				//����ǰ
				wfa.preStarter(params);
				
				response = wf.start(wfDefId, user, wfData, users,viewusers,umWfid,umWorkflow,outTaskNode,tranQueue);				
				//ctx.log(Service.INFO, "wf start refWfInstId=" + (String)umWfid.getString("wfInstId"));
				// ���Ӧ�ý��׵�Message��Succeeded,�����Ӧ����ʾ��Ϣ
				//if (res instanceof ServiceOK) {
					//response = new ServiceOK(wfsi.formatMessage("WF_InWorkflowAndSucceeded", wfsi.getWorkflowDefinition(wfDefId).getString("flowname")));
				
				//������
				wfa.postStarter(params);
				
				//����
				wfa.finish(params);
				
				response = null;
				//}
			} else {
				/*
				 ��λ��ǰ�ڵ� 
				����ҵ������ 
				������ǰ���� 
				ת����һ�ڵ� 
				�������� 
				*/
				
				//��ȡ����
				String flowMainId = request.getField("flowid");
				UnikMap dwflowmainVo = cacheTools.queryDwflowmain(flowMainId);
				String flowtemplate = dwflowmainVo.getString("flowtemplate");
				//ҵ������ģ��
				Class c = Class.forName(flowtemplate);
				WorkFlowAdapter wfa = (WorkFlowAdapter)c.newInstance();
				
				String breakworkflow = request.getField("wf_breakworkflow");				
				//ctx.log(Service.INFO, "[WF_BREAK] workflow engine break. breakworkflow=" + breakworkflow);
				if(breakworkflow==null || breakworkflow.equals("")){
					//ctx.log(Service.DEBUG, "[WF_NORMAL] workflow engine normal.");
					
					//�ύǰ
					wfa.preApprove(params);
					
					response = wf.resume(flowid, user, wfData, users,viewusers,false,blIsDirectForeNode,outTaskNode,tranQueue);
					
					//�ύ��
					wfa.postApprove(params);
				
				}
				else
					if(breakworkflow.equals("breaktonext")){						
						response = wf.breakWorkflowNext(flowid, user, wfData, users,viewusers,false,blIsDirectForeNode,outTaskNode);
						//ctx.log(Service.INFO, "[WF_BREAKTONEXT] workflow engine break.");
					}
					else
						if(breakworkflow.equals("break")){
							response = wf.breakWorkflow(flowid, user, wfData, users,viewusers,false,blIsDirectForeNode);
							//ctx.log(Service.INFO, "[WF_BREAK] workflow engine break.");
						}
				
				ctx.put("outTaskNode", outTaskNode);
				//ctx.getUser().getSession().setAttribute("outTaskNode", outTaskNode);
			}
			long e = System.currentTimeMillis();
			//ctx.log(Service.INFO, "[WF_END] workflow engine close, exec " + JBDate.convertTime(e - b) + fmt(tranCode));
		}

		return response;
	}
}
