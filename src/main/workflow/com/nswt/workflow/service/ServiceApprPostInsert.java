package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.SingleResult;
import cn.com.jbbis.common.BaseService;

/**
 * 北京北大青鸟商用信息系统有限公司
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:流程岗位定义 新增
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: 北京北大青鸟商用信息系统有限公司
 * </p>
 * Service S246I
 * 
 * @author xuecheng@jbbis.com.cn
 * @version 1.0
 * @Date 2008-1-6 上午11:30:09
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
		Assert(rs == null, "common/sysman/ApprPostExist"); //记录已存在
		performAction(INSERT, "dwapprpost");
		return SUCCESS();
	}
}
