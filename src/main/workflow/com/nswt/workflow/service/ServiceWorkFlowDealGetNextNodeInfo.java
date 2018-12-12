package com.nswt.workflow.service;
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.ListResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.jbportal.workflow.Workflow;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;
public class ServiceWorkFlowDealGetNextNodeInfo extends BaseService {

	@Override
	protected AppResponse process() throws Exception {
		WorkflowStorageImpl workflowStorage = new WorkflowStorageImpl(context);
		Workflow workflow = new Workflow(workflowStorage);
		UnikMap wfVars = new UnikMap();//工作流变量
//		LoanServiceContext lsc = new LoanServiceContext(this.getApplication(), this.getContext().getUser(),
//				this.getContext().getAppContext(), "logs");
//		BusiDataLoad dl = new BusiDataLoad(lsc);
//		wfVars.putAll(dl.getFieldInfo());
		String flowid = request.getField("flowid");
		String nodeid = request.getField("nodeid");
		String dealopinValue = request.getField("dealopin");
		String bankid = request.getField("bankid");
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
		boolean isDirectForeNode = false;
		String nextNodeId = workflow.getnextnodeid(flowid, nodeid, wfVars, isDirectForeNode).getString("nextnodeid");
		UnikMap nextNode = workflowStorage.getNodeById(flowid, nextNodeId);
		String autodisuserflag = nextNode.getString("autodisuserflag");
//		System.out.println("-------------------------------》》》》》下一节点任务分配策略" + autodisuserflag);
		user.setCache("s_autodisuserflag", autodisuserflag);
		UnikMap params = new UnikMap();
		params.put("bankid", bankid);
		params.put("postidset", nextNode.getString("postidset"));
		DataList userList = this.executeProcedure("wf.WF0201L1", params);
		String users="";
		for (int i = 0; i < userList.countRows(); i++) {
			users+=userList.getRow(i)[0]+",";
//			System.out.println("--------------------------》》》》》"+i+"users:"+users);
		}
		if(users!=""&&users!=null)
			users=users.substring(0, users.length()-1);
//		System.out.println("--------------------------》》》》》users:"+users);
		user.setCache("s_users", users);
		ListResult lr = new ListResult(userList);
		return lr;
	}
}
