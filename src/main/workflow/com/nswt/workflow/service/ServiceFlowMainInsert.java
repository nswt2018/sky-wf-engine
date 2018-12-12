package com.nswt.workflow.service;
//JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.common.function.SerialNum;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.UnikMap;
/**
* <p>Title: 客户信息管理</p>
* <p>Description:流程定义新增</p>
* <p>Copyright: Copyright (c) 2002</p>
* <p>Company: 北京天桥北大青鸟科技股份有限公司</p>
* @author xuecheng@jbbis.com.cn
* @version 1.0
*/

public class ServiceFlowMainInsert extends BaseService  {

	protected AppResponse process() throws Exception {
		String trancode = contextGetField("trancode");
		//流程以后统一改
		if("copy".equals(trancode)){   //WF0001C
			//从现有流程中拷贝生成另外一个流程
			WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
			UnikMap dp = new UnikMap();
			dp.put("flowname", request.getField("flowname"));

			wfs.copyWFDef(request.getField("flowid"), dp);
		}else{

			SerialNum sn = new SerialNum(this, request);
			
			String str = sn.getGlobalNum("dwflowmain", 10);//表名  序列号长度
			System.out.println(str);
			request.setField("flowid", str);
			performAction(INSERT, "dwflowmain");
		}
		return SUCCESS();
	}
}