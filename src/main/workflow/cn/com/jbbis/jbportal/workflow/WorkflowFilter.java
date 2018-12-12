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
 * 工作流插件
 * <P>
 * 判断是否走流程,以及走哪个流程，在执行每个交易的时候都得先执行doFilter方法,此Filter类在应用里只有一个对象.
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-7 下午05:09:56
 * @since   JBPortal3.0-njloan
 */

public class WorkflowFilter implements AppFilter{
	/**
	 * LoanApp主要用来得到应用的一些对象
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
	 * 在正常交易之前执行此方法。
	 * <ol>
	 * <li>判断是否需要启动工作流程,每个交易进行过滤,svc中配置了wfisrun=false则不走电子审批;
	 * 如果需要走电子审批,则在交易报文中需要存在wfid即工作流实例编号,如果编号值为空则会被视为
	 * 流程的开始节点,什么提交交易走什么流程都需要应用去配置"电子审批范围"</li>
	 * <li>如果下一节点的任务分配是手工分配的话，手工选出的处理人员需要存放在LoanServiceContext
	 * 中，lsc.setProperties("users",用户);否则工作流程引擎将无法取得应用手工选择的人员信息</li>
	 * <li>每个节点的处理意见也都应该存放于LoanServiceContext对象中,否则进行路由判断时没有判断
	 * 的数据，存放方式lsc.setProperties("workflow",处理意见信息)；
	 * 用户和处理意思都是doFilter中取出来传给流程引擎的</li>
	 * 
	 * 如果wfisrun==true && wfselwin==false，则将request中的变量放入Session，记住原始交易Tid001
	 * 定位到选人和意见的交易Tid002
	 * 如果peoplepopion==true，处理流程启动或提交
	 * 执行原始交易Tid001
	 * </ol>
	 */
	public AppResponse doFilter(AppContext arg0, AppFilterChain arg1)
			throws Exception {
		
		// 如果交易的.svc中配置wfisrun=false,则此交易不用走流程引擎
		String isStartWorkflow = arg0.getString("wfisrun");		
		String isWfSelWin = arg0.getString("wfselwin");
		String wfDefId = "";
		String startCreateWfInstId = "";  //流程刚进入的时候，还没有创建流程实例编号（wfid），需要从创建部分返回来
		UnikMap umWfid = new UnikMap();
		AppRequest request = arg0.getRequest();           //请求报文信息
		String tranCode = request.getHeader("tranCode");  //后台服务交易号		
		//交易队列
		Queue<UnikMap> tranQueue = new ArrayDeque<UnikMap>();				
		//arg0.log(Service.INFO, "[WF_doFilter] wfisrun = " + isStartWorkflow + fmt(tranCode));		
		if ("true".equalsIgnoreCase(isStartWorkflow) ) {
//		if ("true".equalsIgnoreCase(isStartWorkflow) || "true".equalsIgnoreCase(isWfSelWin)) {	
//			//第一次提交，isWfSelWin=null，故定向到选人界面；确定后，isWfSelWin=true
//			if(!"true".equalsIgnoreCase(isWfSelWin)) {
////				request.setHeader("tranCode", CommonConst.WF_SEL_WIN);
////				request.setHeader("verb", "IN-EXEC");
////				request.setHeader("tid", CommonConst.WF_SEL_WIN);
////				return arg1.doFilter(arg0);
//				arg0.getAppContext()
//			}
			String wfInstId = request.getString("wfid");  //读取上传报文中的工作流序列号,  注意：初次进入的时候，传入的不是wfid，是flowid
			wfDefId = wfInstId;
			// 如果工作流编号为空,则需要判断当前交易是否需要进入流程,返回流程编号不为空,说明找到了电子审批流程入口，则需要启动新的流程
			if (wfInstId == null || wfInstId.equals("")) {
				wfInstId = new CheckWorkflow(arg0).checkStartCondition(tranCode);
				if (wfInstId != null) {
					wfInstId = "start:" + wfInstId;
					request.setField("workflowisrun", "true"); //主要给应用来判断是否走流程使用,其实应用可以根据wfid来判断
				} else
					request.setField("workflowisrun", "false");
			}else
				request.setField("workflowisrun", "true");
			request.setHeader("wf_workflowid", wfInstId);
		}
		
		//AppResponse res = arg1.doFilter(arg0);            // 调用应用中的交易(节点上配置的交易（浏览、执行、提交）)
				
		AppResponse dores = null;
		if (isStartWorkflow!=null && "true".equalsIgnoreCase(isStartWorkflow)){
		  dores = doFilterAfter(arg0,umWfid,tranQueue);		 
		}
		//arg0.log(Service.INFO, "[WF_doFilter] startCreateWfInstId = " + umWfid.getString("wfInstId") );
		startCreateWfInstId = (String)umWfid.getString("wfInstId");
		//用初次的替换
		if(startCreateWfInstId!=null && !startCreateWfInstId.equals("")){
			wfDefId = startCreateWfInstId;
		}
		if(wfDefId==null || wfDefId.equals("")){
			wfDefId = request.getHeader("wf_workflowid");
			if (wfDefId!=null && wfDefId.startsWith("start:")){							
				//获取流程 编号
				wfDefId = wfDefId.substring(6);
			}
		}
					
		//设置任务信息到Session中，传给提交交易(下一个节点信息和用户信息)
		if(arg0.getUser()!=null && arg0.getUser().getSession()!=null){
		   arg0.getUser().getSession().setAttribute("output_wfid", wfDefId);
		}			
		AppResponse res = arg1.doFilter(arg0);            // 调用应用中的交易(节点上配置的交易（浏览、执行、提交）)
		//从队列中获取交易,并执行
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
						// 如果是普通的交易成功提示,则将此消息进行修改
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
			else if(trantype!=null && trantype.equals("retutrans")){  //退回交易				
					String tCode = node.getString("retutrancode");
					if(tCode!=null && !tCode.equals("")){
						res = doReturnService(arg0,node,var);
					}					
				}
		}		
		//输出节点信息
		return dores == null ? res : dores;
		//return dores;
	}
	/**
	 * 执行退回交易
	 * @param ctx     上下文
	 * @param node    执行时的当前节点
	 * @param varsUm  记录的当时的流程变量
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
	 * 流程结束执行结束交易
	 * @param ctx
	 * @param node    结束交易的节点
	 * @param varsUm  记录的当时的流程变量
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
	 * 正常交易执行之后执行此方法。
	 * <ol>
	 * <li>判断此交易是否要需要走工作流程,主要是从request中取wf_workflowid值。</li>
	 * <li>如果需要走工作流程，再判断是启动新的流程还是直接进入流程引擎,根据wfid来判断。</li>
	 * <li>如果是走工作流程，则从上传报文对象中取出工作流变量workflow，
	 * 路由判断条件或是工作流程引擎中需要的变量都是从workflow这个属性值里取得，
	 * 如果上传报文中没有workflow变量，则会去变量表dwtaskvars中取新的值
	 * 。如果不是开始节点则取出处理人员，节点的任务分配是自动的话处理人员取出的 为null，只有手工分配users才会有值</li>
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
			
			//========获取工作流变量==========			
			//Object object = lsc.getProperties("workflow");
//			if(object==null){
//				object = ctx.getUser().getSession().getAttribute("workflow");
//			}
//			if(object==null){
//				//ctx.log(Service.DEBUG,"you must set var of workflow" );	
//				isException = true;
//				exceptionMessage = "流程变量已丢失或者未设置！";
//				//Assert(object!=null,"请设置流程变量！");
//			}
//			UnikMap wfData = new UnikMap();
//			if (wfData instanceof UnikMap) {
//				wfData.putAll((UnikMap) object);
//			}else
//				//ctx.log(Service.WARN, "workflow data type not UnikMap.");

			/*
			 * 取出工作流变量，是否每次提交都要取一次？ 提交界面上该业务表的所有主键与值要提交！
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
			//报文中取出users信息，如果是手工分配处理人员则users不应该为null,检查用户编号是否合要求
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
//					System.out.println("用户信息:" + users[i]);
//					if (users[i].indexOf("/") < 1) {
//						users = null;
//						break;
//					}
//				}
//			}
//			else{
//				//ctx.log(Service.DEBUG,"get users from Session null"  );
//			}
			
			//报文中取出users信息，如果是手工分配处理人员则users不应该为null,检查用户编号是否合要求
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
			boolean blIsDirectForeNode = false;  //这里应该用全局变量	
			
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
				//获取传入的参数
				UnikMap umWorkflow = (UnikMap)ctx.getUser().getSession().getAttribute("busiworkflow");				
				//获取流程 编号
				String wfDefId = flowid.substring(6);
				
				//开始工作流, umWfid，outTaskNode都是空的，umWorkflow外部传入的业务参数
				
				//获取缓存
				UnikMap dwflowmainVo = cacheTools.queryDwflowmain(wfDefId);
				String flowtemplate = dwflowmainVo.getString("flowtemplate");
				//业务流程模板
				Class c = Class.forName(flowtemplate);
				WorkFlowAdapter wfa = (WorkFlowAdapter)c.newInstance();
				
				//初始化
				wfa.bizInit(params);
				
				//启动前
				wfa.preStarter(params);
				
				response = wf.start(wfDefId, user, wfData, users,viewusers,umWfid,umWorkflow,outTaskNode,tranQueue);				
				//ctx.log(Service.INFO, "wf start refWfInstId=" + (String)umWfid.getString("wfInstId"));
				// 如果应用交易的Message是Succeeded,则更改应用提示信息
				//if (res instanceof ServiceOK) {
					//response = new ServiceOK(wfsi.formatMessage("WF_InWorkflowAndSucceeded", wfsi.getWorkflowDefinition(wfDefId).getString("flowname")));
				
				//启动后
				wfa.postStarter(params);
				
				//结束
				wfa.finish(params);
				
				response = null;
				//}
			} else {
				/*
				 定位当前节点 
				保存业务数据 
				结束当前任务 
				转至下一节点 
				分派任务 
				*/
				
				//获取缓存
				String flowMainId = request.getField("flowid");
				UnikMap dwflowmainVo = cacheTools.queryDwflowmain(flowMainId);
				String flowtemplate = dwflowmainVo.getString("flowtemplate");
				//业务流程模板
				Class c = Class.forName(flowtemplate);
				WorkFlowAdapter wfa = (WorkFlowAdapter)c.newInstance();
				
				String breakworkflow = request.getField("wf_breakworkflow");				
				//ctx.log(Service.INFO, "[WF_BREAK] workflow engine break. breakworkflow=" + breakworkflow);
				if(breakworkflow==null || breakworkflow.equals("")){
					//ctx.log(Service.DEBUG, "[WF_NORMAL] workflow engine normal.");
					
					//提交前
					wfa.preApprove(params);
					
					response = wf.resume(flowid, user, wfData, users,viewusers,false,blIsDirectForeNode,outTaskNode,tranQueue);
					
					//提交后
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
