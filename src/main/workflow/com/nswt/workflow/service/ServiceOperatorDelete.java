package com.nswt.workflow.service;

// JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>
 * Title:用户基本信息
 * </p>
 * <p>
 * Description:新增用户基本信息
 * </p>
 * <p>
 * Copyright: Copyright(c) 2008
 * </p>
 * <p>
 * Compny: 北京北大青鸟商用信息系统有限公司
 * </p>
 * Service S103D
 * 
 * @author GuoHan
 * @version 1.0
 * @Date 2008-2-25 上午10:08:26
 */

public class ServiceOperatorDelete extends BaseService {
	

	protected AppResponse process() throws Exception {
		String UserAcc = request.getString("useracc");
		String UserId = request.getString("userid");
		String BankId = request.getString("BankId");
		
		UnikMap uMap = new  UnikMap();
		uMap.put("userid", UserId);
		uMap.put("BankId", BankId);
		String str = "";		
		DataList CustList  = executeProcedure(getTpl("getCustInfo"), uMap);
		DataList ContList  = executeProcedure(getTpl("getContInfo"), uMap);
		DataList LoanList  = executeProcedure(getTpl("getLoanBalInfo"), uMap);
		DataList MessageList  = executeProcedure(getTpl("getMessageInfo"), uMap);
		DataList TaskList  = executeProcedure(getTpl("getTaskInfo"), uMap);
		if (CustList.countRows() >0) {str ="该用户有管辖客户，不能删除用户 \n";}
		if (ContList.countRows() >0) {str =str + "该用户名下存在生效中的合同，不能删除用户\n";	}
		if (LoanList.countRows() >0) {str =str + "该用户名下存在未结清借据，不能删除用户\n";}
		if (MessageList.countRows() >0) {str =str + "该用户名下存在未处理待办任务，不能删除用户\n";}	
		if (TaskList.countRows() >0) {str =str + "该用户名下存在未处理流程任务，不能删除用户\n";}		
		if (CustList.countRows() >0 || ContList.countRows() >0 || LoanList.countRows() >0 || MessageList.countRows() >0 ||TaskList.countRows() >0) {
			Assert(false, str);
		}

		performAction(DELETE, "cmuser",uMap);

		
		return SUCCESS();
	}
}