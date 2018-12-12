package com.nswt.workflow.service;
//JDK
import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.common.function.SerialNum;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.UnikMap;
/**
* <p>Title: �ͻ���Ϣ����</p>
* <p>Description:���̶�������</p>
* <p>Copyright: Copyright (c) 2002</p>
* <p>Company: �������ű�������Ƽ��ɷ����޹�˾</p>
* @author xuecheng@jbbis.com.cn
* @version 1.0
*/

public class ServiceFlowMainInsert extends BaseService  {

	protected AppResponse process() throws Exception {
		String trancode = contextGetField("trancode");
		//�����Ժ�ͳһ��
		if("copy".equals(trancode)){   //WF0001C
			//�����������п�����������һ������
			WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
			UnikMap dp = new UnikMap();
			dp.put("flowname", request.getField("flowname"));

			wfs.copyWFDef(request.getField("flowid"), dp);
		}else{

			SerialNum sn = new SerialNum(this, request);
			
			String str = sn.getGlobalNum("dwflowmain", 10);//����  ���кų���
			System.out.println(str);
			request.setField("flowid", str);
			performAction(INSERT, "dwflowmain");
		}
		return SUCCESS();
	}
}