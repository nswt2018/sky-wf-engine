package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.ListResult;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.common.LoanServiceContext;
import cn.com.jbbis.common.template.ServiceNoResuSelect;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

public class ServiceGetCurrentNodePost extends BaseService{

	protected  AppResponse process() throws Exception {
		super.processRequestFields();
		String flowid=request.getField("flowid");
		//如果为空或""则说明是从WF0201F中调用的该交易
		if(flowid==""||flowid==null){
			request.setField("flowid", user.getCache("s_flowid"));
			request.setField("nodeid", user.getCache("s_nodeid"));
		}
		SingleResult sr = querySingle("wf.WF0121S1",request);
		sr.setField("autodisuserflag", user.getCache("s_autodisuserflag"));
		sr.setField("users", user.getCache("s_users"));
		return sr;
	}
}
