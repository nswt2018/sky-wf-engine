package com.nswt.workflow.service;

import java.util.Iterator;
import cn.com.jbbis.afx.AppRequest;
import cn.com.jbbis.afx.Application;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.jbportal.Service;
import cn.com.jbbis.jbportal.ServiceContext;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.Errors;
import cn.com.jbbis.util.StringUtil;
import cn.com.jbbis.util.UnikMap;
/**
 * Title:
 * Description:
 * Copyright: Copyright (c) 2008
 * Company: �������ű�������Ƽ��ɷ����޹�˾
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-7-10 ����08:36:23 Service
 */
public class WorkFlowUserList {
	private Service service;
	private ServiceContext context;
	private AppRequest request;
	private Application application;
	
	public WorkFlowUserList(Service ser, AppRequest req,ServiceContext con) {
		service = ser;
		request = req;
		context = con;
		application = con.getApplication();
	}
	public WorkFlowUserList(Service ser,ServiceContext con){
		this(ser, con.getRequest(), con);
	}
	
	/**
	 * @discription:�����û��б�
	 * @param:
	 * @return:
	 * @throws Exception
	 */
	public DataList getUserId(String postauthority) throws Exception {
		    
		String wfid = request.getField("fwid");
		String flowId = request.getField("flowid");
		String bankId = context.getUser().getCache("bankid");
		String userId = context.getUser().getCache("operid");
		
		request.setField("fid", wfid);
		String nextNodeId = request.getField("nextnodeid");
		UnikMap dp = new UnikMap();
		dp.put("flowid", flowId);
		dp.put("nodeid", nextNodeId);
		dp.put("postauthority", postauthority);
		
		//WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
		UnikMap paras = new UnikMap();
			//paras.putAll(wfs.getNodePost(dp));
		if(paras==null)
		{
			
			return null;
		}
		String org = paras.getString("bankid");
		String postIdSet = paras.getString("PostIdSet");
		String SuperBankScope = paras.getString("SuperBankScope");
		String bankUserId = bankId + "/" + userId;
		paras.put("local", bankUserId);
		paras.put("post", postIdSet);
		paras.put("nodeid", nextNodeId);
		paras.put("org", org);
		paras.put("wfid", wfid);
		paras.put("hisflag", request.getString("hisflag"));
		paras.put("superbankscope", SuperBankScope);
		
		//String[] s = wfs.selectUsers(paras,true);
		//for(int i=0;i<s.length;i++)
		//System.out.println("userList=====================" + s[i]);
		
		
		//�������̱�ţ�%1 &#10;�ڵ� [%2] Ŀǰû���ڸ���Ա,�����ύҵ��
		//if(s == null){
		//	return null;
		//}
		DataList dl = new DataList();
		//���usrhisflag�û��Ƿ���ʷ�û�,�������ʷ���ҵ����û���usrhisflag=true,����=false;
		//2009-03-27 kangshangjun
		dl.addColumns("userid,username,usrhisflag".split(","));
		/*for(int i = 0;i < s.length;i++){
			String[] val = new String[3];
			String[] v = s[i].split("\\|");
			val[0] = v[0];
			//val[1] = v[1];
			String[] userid =v[0].split("/");
			val[1] = userid[1] + "-" + v[1];
			
			val[2] = paras.getString("usrhisflag");
			dl.addRow(val);
		}*/
		return dl; 
	}
	/**
	 * @discription:�����û��б�
	 * @param:
	 * @return:
	 * @throws Exception
	 */
	public DataList getUserId() throws Exception {
		    
		String wfid = request.getField("fwid");
		String flowId = request.getField("flowid");
		String bankId = context.getUser().getCache("bankid");
		String userId = context.getUser().getCache("operid");
		
		request.setField("fid", wfid);
		String nextNodeId = request.getField("nextnodeid");
		UnikMap dp = new UnikMap();
		dp.put("flowid", flowId);
		dp.put("nodeid", nextNodeId);
				
		//WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
		UnikMap paras = new UnikMap();
			//paras.putAll(wfs.getNodePost(dp));
		if(paras==null)
			System.out.println("paras====null");
		String org = paras.getString("bankid");
		String postIdSet = paras.getString("PostIdSet");
		String bankUserId = bankId + "/" + userId;
		paras.put("local", bankUserId);
		paras.put("post", postIdSet);
		paras.put("nodeid", nextNodeId);
		paras.put("org", org);
		paras.put("wfid", wfid);
		paras.put("hisflag", request.getString("hisflag"));
		
		//String[] s = wfs.selectUsers(paras,true);
	/*	for(int i=0;i<s.length;i++)
		System.out.println("userList=====================" + s[i]);*/
		
		
		//�������̱�ţ�%1 &#10;�ڵ� [%2] Ŀǰû���ڸ���Ա,�����ύҵ��
		/*if(s == null){
			return null;
		}*/
		DataList dl = new DataList();
		//���usrhisflag�û��Ƿ���ʷ�û�,�������ʷ���ҵ����û���usrhisflag=true,����=false;
		//2009-03-27 kangshangjun
		dl.addColumns("userid,username,usrhisflag".split(","));
		/*for(int i = 0;i < s.length;i++){
			String[] val = new String[3];
			String[] v = s[i].split("\\|");
			val[0] = v[0];
			val[1] = v[1];
			val[2] = paras.getString("usrhisflag");
			dl.addRow(val);
		}*/
		return dl; 
	}
	/**
	 * @discription:����Ӧ�õı�־dwFlowMain.MenuSql
	 * ����:execSql   �ж�����:menusql
	 * state=trueΪ�ж�����,state=falseΪ�˻ز�������,ʹ����ȡ�غ��˻���������,
	 * ��,����ʼ���������ȡ�غ͵�ĳһ���������˻ص���ʼ�ڵ�ʱ��״̬��Ϊ�˻ز�������
	 * @param:boolean state״̬
	 * @param:String wfid
	 * @throws Exception
	 */
	public void delaExecSql(String wfid,boolean state) throws Exception{
		UnikMap vars = new UnikMap();
		String wfInstId = wfid;
		/*
		 <sql name="getAllDwtaskvars">
		 	select 
		 		varname,varvalue 
		 	from 
		 		dwtaskvars
		 	where
		 	 	wfid = @wfid
		 	order by taskser asc
		 </sql>
		 */
		UnikMap dp = new UnikMap();
		dp.put("wfid", wfInstId);		
		DataList dl = service.executeProcedure("jbbis.common.workflow.Service2Manual_getAllDwtaskvars",dp);
		while(dl.next()){
			//���㷨��֤��ȡ�ı��������±����
			String name = dl.getString("varname");
			String value = dl.getString("varvalue");
			vars.put(name,value);			
		}		
		/*
		 <sql name="getAllDwflowinst">
		 	select 
		 		execsql,manusql 
		 	from 
		 		dwflowinst
		 	where
		 	 	wfid = @wfid
		 	order by taskser asc
		 </sql>
		 */
		SingleResult sr = service.querySingle("jbbis.common.workflow.Service2Manual_getAllDwflowinst",dp,null);	
		Errors.Assert(sr != null, "common/workflow/WF_NotFoundFlowInst");//����ʵ��������
		String sql = sr.getField("execsql").toLowerCase();	//����
		String sqlName = "����";
		if(state){
			sql = sr.getField("manusql");	//ת�ֹ�
			sqlName = "ת�ֹ�";
		}
		Errors.Assert(!"".equals(sql), application.formatMessage("common/workflow/WF_NotFoundManuSql",sqlName));//�������̶��彻���Ƿ�����ת�ֹ�sql
	
		Iterator it = vars.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = vars.getString(name);
			sql = StringUtil.replace(sql, "{" + name + "}", value);
		}
		
		dp.clear();
		dp.put("procedure", sql);
		try {
			service.executeProcedure("COMMON", dp);
		} catch (RuntimeException e) {
			//���ԱManuSql����ȷ��
			Errors.Assert(false, application.formatMessage("common/workflow/WF_ErrorManuSql",sql));
		}
	}
}
