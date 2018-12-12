package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;

/**
 * ������������������Ϣϵͳ���޹�˾
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:���̸�λ���� ����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: ������������������Ϣϵͳ���޹�˾
 * </p>
 * Service S246I
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-1-6 ����11:30:09
 */
public class ServiceApprPostInsert extends BaseService{

	protected AppResponse process() throws Exception {
		/*
		 <sql name="getPostid">
		 select 
		 postid
		 from
		 dwapprpost
		 where
		 postid=@postid
		 </sql>
		 */
		SingleResult rs = querySingle(getWorkFlowTpl("getPostid"));
		Assert(rs == null, "common/sysman/ApprPostExist"); //��¼�Ѵ���
		performAction(INSERT, "dwapprpost");
		return SUCCESS();
	}
}
