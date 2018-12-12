/*
 * @(#)CheckWorkflow.java
 * 
 * Beijing Beida Jade Bird Business Information System Co.,Ltd
 *
 * Copyright (c) 2009 JBBIS. All Rights Reserved.
 *
 * http://www.jbbis.com.cn
 */

package cn.com.jbbis.jbportal.workflow;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import cn.com.jbbis.afx.AppContext;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.Application;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.ehcahe.EhcacheTools;
import cn.com.jbbis.common.ehcahe.vo.CmbankVo;
import cn.com.jbbis.common.ehcahe.vo.InterfaceVo;
import cn.com.jbbis.jbportal.BizLogic;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.Errors;
import cn.com.jbbis.util.UnikMap;

/**
 * �������������.
 * <P>
 *
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-7 ����05:22:54
 * @since   JBPortal3.0
 */

public class CheckWorkflow extends BizLogic{
	private AppContext ctx;
	private Application app;
	private static Object obj;
	public CheckWorkflow(AppContext context){
		set(context);
		ctx = context;
		this.app = context.getApplication();
	}
	
	/**
	 * �жϵ�ǰ�����Ƿ�������.
	 * @param tranCode ���׺�
	 * @return ������򷵻����̱��,���򷵻�null
	 * @throws Exception
	 */
	public String checkStartCondition(String tranCode) throws Exception{
		String flowid = null;
		Properties prop = loadConf(ctx, tranCode);
		if(prop != null){
			flowid = prop.getProperty(tranCode);
		}
		return flowid;
	}
	
	/**
	 * �ж��Ƿ��ǵ�����������,��Ҫ�Ƕ�ȡCmBankElecScope���е�������Ϣ
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
    private Properties loadConf(AppContext ctx, String tranCode) throws Exception {
    	//�жϴ˽����Ƿ������� begin add by kangsj	
    	//  prop.put(htb.get("submtrancode"), htb.get("flowid"));
		
    	Properties pt = new Properties();
    	//tranCode  �ύ����
    	// request.getField("s_bankid") ������
    	//request.getField("prodid")  ��Ʒ��
    	
    	String prodid = request.getField("prodid");    	
    	/////////////debug ////////////////
    	if(prodid==null || "".equals(prodid)) 
    	{
    		log(ERROR, "�������Prodid����Ϊ�գ�");
    	}	
    	///////////////////////////////////
		EhcacheTools cacheTools = new EhcacheTools();
		long beginTime_ = System.currentTimeMillis();
		List<InterfaceVo>list = EhcacheTools.queryBankInfo(request.getField("s_operid"), "1");
		
		long endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryBankInfo cost:"+(endTime_-beginTime_));
		
		
		String[] logbankinfoArray = null;
		if(list!=null && list.size()>0) {
			logbankinfoArray = cacheTools.transBankid(list);
		}else{
			logbankinfoArray = new String[]{request.getField("s_bankid")};
		}
		
		beginTime_ = System.currentTimeMillis();
		UnikMap u = cacheTools.queryCmbankelecscopeVo(logbankinfoArray, prodid, tranCode);
		endTime_ = System.currentTimeMillis();
		log(Service.INFO, "---------get cache queryCmbankelecscopeVo cost:"+(endTime_-beginTime_));
		
		if(u==null) {
			return null;
		}
		
    	pt.put(tranCode, u.getString("flowid"));
    	
		ctx.log(Service.INFO, "[loadConf NO:2] tranCode: " + tranCode+", value is " + pt.toString());
		
		//flowdi
		return pt;
	}
    
    /**
     * table ������Ŵ���˵���������Χÿ����¼�������ֶε�ֵ��table��һ����¼��
     * 
	 * ���cmBankElecScope���д˽������ĸ�����,
	 * ���֧�л����û���ҵ���Ӧ�ļ�¼��������ϼ���,�ݹ����
	 * ����Ǵ�����������,�����ݲ�Ʒȥ���ҵ���������Χ��,����
	 * ҵ�������������ʱֻ���ݻ�����ȥ����,���ͬһ��Ʒ��������
	 * ���ϼ����������������̣����Ա�������Ϊ��
	 * @since JBPortal 3.0
	 * @throws java.lang.Exception
	 */
    private void iselecappr(Hashtable table, AppContext ctx,
			Properties prop, String bankid, boolean flag) throws Exception {
		if (flag == true)
			return;
		Enumeration en = table.elements();
		AppRequest request = ctx.getRequest();
		int lever = 0;
		
		while (en.hasMoreElements()) 
		{
			Hashtable htb = (Hashtable) en.nextElement();
			if (bankid.equals(htb.get("bankid"))) {
				//flowtypeֵ��1,˵���Ǵ�����������,��������������Ҫƥ���Ʒ���    ����ҵ������
				Object flowType = htb.get("flowtype");

				//�����Ƿ�󶨵�ҵ��������  LIUXJ
			
					if (CommonConst.WF_TYPE_LOANAPPLY.equals(flowType) 							
							//|| CommonConst.WF_TYPE_LOANAFTER.equals(flowType)
							|| CommonConst.WF_TYPE_PLOANAPPLY.equals(flowType)) {   //ҵ������Ϊ���������Թ���������
						//ͨ��loanid��ȡ��Ʒ���
						String proId = request.getField("prodid");  //getProdId(request);
						
						
						//����˲�ƷID�����ݱ��еĲ�ƷID����ͬ,�����
						if (!proId.equals(htb.get("prodid"))) {
							continue;
						}
					} else
					
					//2012-09-25������������У������˻��������Ҫ���⴦�����ݲ�Ʒ�Ĳ�ͬȥ�߲�ͬ������
					if (CommonConst.WF_TYPE_LOANAFTER.equals(flowType)){
						String proId = request.getField("prodid");
						//�ж��Ƿ��ǰ󶨲�Ʒ����Ҫ������˻��������Ŀǰ���˻������������������̶��ǰ�ҵ���
						String flowbindstyle = htb.get("flowbindstyle").toString();
						
						if ("1".equals(flowbindstyle)) {
							//����˲�ƷID�����ݱ��еĲ�ƷID����ͬ,�����
							if (!proId.equals(htb.get("prodid"))) {
								continue;
							}
						}
					}
					//2012-09-25 end.
					//2014-01-14  չ����ֹ����ͬһ���ύ����.ͬһ�����Բ�ͬҵ�����ߵ�չ����ֹ�ò�Ʒ������
					else if(CommonConst.WF_TYPE_CONTAPPR.equals(flowType)){
						String proId = request.getField("prodid");
						//�ж��Ƿ��ǰ󶨲�Ʒ
						String flowbindstyle = htb.get("flowbindstyle").toString();
						
						if ("1".equals(flowbindstyle)) {
							//����˲�ƷID�����ݱ��еĲ�ƷID����ͬ,�����
							if (!proId.equals(htb.get("prodid"))) {
								continue;
							}
						}
					}
					//2014-01-14 end
					String flowtype = request.getField("flowtype");
					
					if(!"".equals(flowtype)){
						if(!flowtype.equals(htb.get("flowtype"))){
							continue;
						}
					}
					if ("1".equals(htb.get("iselecapprflag")))
						prop.put(htb.get("submtrancode"), htb.get("flowid"));
					flag = true;
					break;

		    } 
			else 
			{
				continue;
			}
			
		}
		//���û��s_banklevel���ܻ���ѭ��,����5���Զ��˳�,����������������б����ж�Ӧ���д��ֶε�
		if (flag == false && lever < 5
				&& !CommonConst.BANK_LEVEL_FIRST.equals(request.getField("s_banklevel"))) {
			lever++;
			String superbankid = getsuperbankid(ctx, bankid);
			flag = superbankid.equals(bankid) ? true : false;
			iselecappr(table, ctx, prop, superbankid, flag);
		}
    }
    
    /*
     * �ӽ����л�ȡ��Ʒ���
    */
    private String getProdId(UnikMap request) throws Exception{
    	//��һ�β��ҵ�ʱ����û�в�ƷID��
		String wfProdid = request.getString("wf_prodid");
		if(wfProdid == null){
			wfProdid = request.getString("prodid");
			if(wfProdid == null){
				// �ҳ���Ʒ��ŷ����ϴ����Ķ�����
				/*
				 * <sql name="getProdidFromDbapp"> 
				 *     select 
				 *     		prodid 
				 *     from
				 *          dbapp 
				 *     where 
				 *     		loanid = @ loanid 
				 * </sql>
				 */
				UnikMap params = new UnikMap();
				params.put("loanid", request.getString("loanid"));
				SingleResult sr = null;
				//�Թ��͸������ò�ͬ��TPL   liuxj
                if(CommonConst.WF_TYPE_PLOANAPPLY.equals(request.getString("flowtype"))){  //�Թ�����
                  // sr = querySingle(getTpl("getProdidFromCorpDbapp"), params, null);
                    sr = querySingle(getTpl("getProdidFromdbAppApprScheme"), params, null);  // dbAppApprScheme
                }
                else 
                	if(CommonConst.WF_TYPE_LOANAPPLY.equals(request.getString("flowtype"))){  //��������
                      
                		//sr = querySingle(getTpl("getProdidFromDbapp"), params, null);
                		 sr = querySingle(getTpl("getProdidFromdbAppApprScheme"), params, null);  // dbAppApprScheme
                    } 
				
                //��dbapp����û���ҵ���Ʒ���
				Errors.Assert(sr != null, app.formatMessage("workflow/NotFoundProdid", request.getString("loanid")));
                
				wfProdid = sr.getString("prodid");
			}
			
			request.put("wf_prodid", wfProdid);
		}
		return wfProdid;
    }
    
    
    /**
	 * ���ݻ��������ѯ�ϼ�������
	 * 
	 * @param bankid
	 *            ��ǰ��������
	 * @return String �ϼ���������
	 * @throws java.lang.Exception
	 */
    public String getsuperbankid(AppContext ctx, String bankid) throws Exception {
    	UnikMap dp = new UnikMap();
		dp.put("bankid", bankid);
		SingleResult sr = querySingle("jar:cn.com.jbbis.jbportal.workflow.procedure.WorkflowStorageImpl_getAllCmbank",dp, null);

		Errors.Assert(sr != null, app.formatMessage("workflow/SuperbankidNotFound", bankid));

		return sr.getField("superbankid");
	}
    
    /**
	 * ��ȡcmBankElecScope���е����ô洢��Application,�����д�ŵ���Properties,����Ϊiselecappr_config
	 * ��Ҫ�ǽ��ύ���״���Ӧ�û���,��������Ƶ����ѯ��ϵͳ������ѹ��,��
	 * �������б��,��Ӧ���ô˷������»���
	 * @throws java.lang.Exception
	 */
    public Properties setBankElecScope(AppRequest request) throws Exception{
    	/*
		 <sql name="getAllCmBankElecScope">
		 	select 
				@asis:SelectField 
			from 
				cmBankElecScope 
			where
				1=1
				#AND =@submtrancode
			group by submtrancode
		 </sql>
		 */

		DataList dlist = executeProcedure(getTpl("getSubmtrancodeFromCmBankElecScope"), request);

		Properties p = new Properties();
		
		while (dlist.next()) {
			String submtrancode = dlist.getString("submtrancode");
			p.put(submtrancode, submtrancode);
		}
		
		//app.setCache("iselecappr_config", p);
		obj=p;
		return p;
    }
    
    private String fmt(String s) {
		return " [" + s + "]";
	}
    
    /**
	 * ȡTPL����,���ص���jar:cn.com.jbbis.jbportal.workflow.ClassName_procname
	 * @param procname TPL����
	 * @throws java.lang.Exception
	 */
	private String getTpl(String procname) throws Exception{
		StringBuffer className = new StringBuffer(this.getClass().getName());
		int index = className.indexOf("cn.com.jbbis.");
		if (index != -1){
			className.insert(className.lastIndexOf(".")+1, "procedure.");
			return "jar:" + className + "_" + procname;
		}else{
			return "jbbis" + procname;
		}
	}
}
