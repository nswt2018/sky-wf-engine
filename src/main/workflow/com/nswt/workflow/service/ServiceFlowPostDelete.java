package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

public class ServiceFlowPostDelete extends BaseService{

	protected AppResponse process() throws Exception {
		String postId = request.getField("postId");
		UnikMap dp = new UnikMap();
		dp.put("postid", postId);
		/*
		 * <sql name="getPostid">
		 * 		select postid from dwflownodepost 
		 * 		where postid=@postid
		 * 		union
		 * 		select postidset postid from cmuser 
		 * 		where postidset=@postid or postidset like @'%,{postid},' 
		 *		or postidset like @'%,{postid}' or postidset like @'{postid},%'
		 * </sql>
		*/
		DataList dl = executeProcedure(getWorkFlowTpl("getPostid")); 
		int num = dl.countRows();
		//岗位编号已经应用
		Assert(num == 0, formatMessage("common/workflow/FlowNodePostAlreadyApp",postId));
		performAction(DELETE,"dwapprpost");
		return SUCCESS();
	}

}
