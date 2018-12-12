package com.nswt.workflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.ListResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.jbportal.workflow.BusiDataLoad;
import cn.com.jbbis.jbportal.workflow.CheckWorkflow;
import cn.com.jbbis.jbportal.workflow.CommonConst;
import cn.com.jbbis.jbportal.workflow.Workflow;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;
import cn.com.jbbis.common.LoanServiceContext;

public class ServiceWorkFlowStartGetNextNodeInfo extends BaseService{

	@Override
	protected AppResponse process() throws Exception {
		WorkflowStorageImpl workflowStorage = new WorkflowStorageImpl(context);
		Workflow workflow = new Workflow(workflowStorage);
		UnikMap wfVars = new UnikMap();
//		LoanServiceContext lsc = new LoanServiceContext(this.getApplication(), this.getContext().getUser(),
//						this.getContext().getAppContext(), "logs");
//		BusiDataLoad dl = new BusiDataLoad(lsc);
//		wfvars.putAll(dl.getFieldInfo());
		String dealopinValue = request.getField("dealopin");
		String bankid = request.getField("s_bankid");
		String prodid= request.getField("prodid");
		String submTranCode=request.getField("submTranCode");
		String dealopin="同意";
		switch (dealopinValue)
		{
		case "20":
			dealopin="不同意";
			break;
		case "30":
			dealopin="再议";
			break;
		}
		wfVars.put("dwopinion.dealopin", dealopin);
		//填充context,查找flowid
		this.context.put("prodid", prodid);
		this.context.put("bankid", bankid);
		String flowid= new CheckWorkflow(this.context).checkStartCondition(submTranCode);
		//获取开始节点
		UnikMap startNode=workflowStorage.getNodeByType(flowid,CommonConst.WF_NODETYPE_START);
		boolean isDirectForeNode = false;
		String nodeid=startNode.getString("nodeid");
		//获取下一节点Id,然后获取下一节点的信息
		String nextnodeid = workflow.getnextnodeid(flowid, nodeid, wfVars, isDirectForeNode).getString("nextnodeid");
		UnikMap nextNode = workflowStorage.getNodeById(flowid, nextnodeid);
		String autodisuserflag = nextNode.getString("autodisuserflag");
//		System.out.println("--------------------------》》》》》下一节点任务分配策略" + autodisuserflag);
		//将autodisuserflag，flowid，nodeid放入session
		user.setCache("s_flowid", flowid);
		user.setCache("s_nodeid", nodeid);
		user.setCache("s_autodisuserflag", autodisuserflag);
		UnikMap vars = new UnikMap();
		vars.put("bankid", bankid);
		vars.put("postidset", nextNode.getString("postidset"));
		//根据bankid和postidset查找下一节点的用户
		DataList userList = this.executeProcedure("wf.WF0201L1", vars);
		String users="";
		for (int i = 0; i < userList.countRows(); i++) {
			users+=userList.getRow(i)[0]+",";
//			System.out.println("--------------------------》》》》》"+i+"users:"+users);
		}
		if(users!=""&&users!=null)
			users=users.substring(0, users.length()-1);
//		System.out.println("--------------------------》》》》》users:"+users);
		//将users信息放入session，为的是在自动分配时，前台不显示选人界面，无法为users设值的问题。
		//放入session中后，可在js中利用WF035S交易在后台获取session中的users，并在提交时将users的值设置为session中的值。
		user.setCache("s_users", users);
		ListResult lr = new ListResult(userList);
		return lr;
	}
}
