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
 * Company: 北京天桥北大青鸟科技股份有限公司
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-7-10 下午08:36:23 Service
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
	 * @discription:查找用户列表
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
		
		
		//工作流程编号：%1 &#10;节点 [%2] 目前没有在岗人员,不能提交业务！
		//if(s == null){
		//	return null;
		//}
		DataList dl = new DataList();
		//填加usrhisflag用户是否历史用户,如果从历史中找到了用户则usrhisflag=true,否则=false;
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
	 * @discription:查找用户列表
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
		
		
		//工作流程编号：%1 &#10;节点 [%2] 目前没有在岗人员,不能提交业务！
		/*if(s == null){
			return null;
		}*/
		DataList dl = new DataList();
		//填加usrhisflag用户是否历史用户,如果从历史中找到了用户则usrhisflag=true,否则=false;
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
	 * @discription:处理应用的标志dwFlowMain.MenuSql
	 * 回退:execSql   中断流程:menusql
	 * state=true为中断流程,state=false为退回补充资料,使用在取回和退回两个操作,
	 * 即,当开始任务结点进行取回和当某一个任务结点退回到开始节点时将状态改为退回补充资料
	 * @param:boolean state状态
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
			//此算法保证读取的变量是最新保存的
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
		Errors.Assert(sr != null, "common/workflow/WF_NotFoundFlowInst");//流程实例不存在
		String sql = sr.getField("execsql").toLowerCase();	//回退
		String sqlName = "回退";
		if(state){
			sql = sr.getField("manusql");	//转手工
			sqlName = "转手工";
		}
		Errors.Assert(!"".equals(sql), application.formatMessage("common/workflow/WF_NotFoundManuSql",sqlName));//请检查流程定义交易是否设置转手工sql
	
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
			//检查员ManuSql的正确性
			Errors.Assert(false, application.formatMessage("common/workflow/WF_ErrorManuSql",sql));
		}
	}
}
