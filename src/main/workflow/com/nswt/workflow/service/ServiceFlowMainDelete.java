package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;

/**
* <p>Title: ���̹���</p>
* <p>Description: ɾ������</p>
* <p>Copyright: Copyright (c) 2002</p>
* <p>Company: �������ű�������Ƽ��ɷ����޹�˾</p>
* @author xuecheng@jbbis.com.cn
* @version 1.0
* Service S801D
*/
public class ServiceFlowMainDelete extends BaseService {

	protected AppResponse process() throws Exception {
		String trancode = contextGetField("trancode");
		//����������δ����
		if("delAll".equals(trancode)){	//ɾ����������
			WorkflowStorageImpl wfs = new WorkflowStorageImpl(context);
			wfs.deleteWFDef(request.getField("flowid"));
		}else{
			performAction(DELETE, "dwflowmain");
		}
		return SUCCESS();
	}
}