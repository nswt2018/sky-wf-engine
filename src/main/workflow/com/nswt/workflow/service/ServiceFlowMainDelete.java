package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;

/**
* <p>Title: 流程管理</p>
* <p>Description: 删除流程</p>
* <p>Copyright: Copyright (c) 2002</p>
* <p>Company: 北京天桥北大青鸟科技股份有限公司</p>
* @author xuecheng@jbbis.com.cn
* @version 1.0
* Service S801D
*/
public class ServiceFlowMainDelete extends BaseService {

	protected AppResponse process() throws Exception {
		String trancode = contextGetField("trancode");
		//以下流操作未处理
		if("delAll".equals(trancode)){	//删除整个流程
			WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
			wfs.deleteWFDef(request.getField("flowid"));
		}else{
			performAction(DELETE, "dwflowmain");
		}
		return SUCCESS();
	}
}