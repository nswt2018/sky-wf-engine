package com.nswt.workflow.service;

// JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>
 * Title:�û�������Ϣ
 * </p>
 * <p>
 * Description:�����û�������Ϣ
 * </p>
 * <p>
 * Copyright: Copyright(c) 2008
 * </p>
 * <p>
 * Compny: ������������������Ϣϵͳ���޹�˾
 * </p>
 * Service S103D
 * 
 * @author GuoHan
 * @version 1.0
 * @Date 2008-2-25 ����10:08:26
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
		if (CustList.countRows() >0) {str ="���û��й�Ͻ�ͻ�������ɾ���û� \n";}
		if (ContList.countRows() >0) {str =str + "���û����´�����Ч�еĺ�ͬ������ɾ���û�\n";	}
		if (LoanList.countRows() >0) {str =str + "���û����´���δ�����ݣ�����ɾ���û�\n";}
		if (MessageList.countRows() >0) {str =str + "���û����´���δ����������񣬲���ɾ���û�\n";}	
		if (TaskList.countRows() >0) {str =str + "���û����´���δ�����������񣬲���ɾ���û�\n";}		
		if (CustList.countRows() >0 || ContList.countRows() >0 || LoanList.countRows() >0 || MessageList.countRows() >0 ||TaskList.countRows() >0) {
			Assert(false, str);
		}

		performAction(DELETE, "cmuser",uMap);

		
		return SUCCESS();
	}
}