/*
 * @(#)ServiceParticipantSelect.java
 * 
 * Beijing Beida Jade Bird Business Information System Co.,Ltd
 *
 * Copyright (c) 2009 JBBIS. All Rights Reserved.
 *
 * http://www.jbbis.com.cn
 */

package com.nswt.workflow.service;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.ListResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.jbportal.workflow.WorkflowStorageImpl;
import cn.com.jbbis.util.DataList;
import cn.com.jbbis.util.UnikMap;

/**
 * 查找处理人员.
 * <P>
 * service WF030L
 * @author  kangsj@jbbis.com.cn
 * @version 2.0, 2009-5-12 下午03:44:47
 * @since   JBPortal3.0
 */

public class ServiceParticipantSelect extends BaseService{

	protected AppResponse process() throws Exception {
		
		
		System.out.println("===========================================ServiceParticipantSelect" );
		String flowid = request.getField("flowid");
		String nodeid = request.getField("nodeid");
		String dutyflag = request.getField("dutyflag");
		
		//liuxj
		request.setField("nextnodeid", nodeid);
		
		WorkFlowUserList wul = new WorkFlowUserList(this, request, context);
		
		String nextNodeId = request.getField("nextnodeid");
		
		System.out.println("==ServiceParticipantSelect=============flowid" + flowid + ";nodeid=" + nodeid  + ";nextNodeId=" + nextNodeId);
		//WorkflowStorageImpl wfsi = new WorkflowStorageImpl(this, context);
		//UnikMap node = wfsi.getNodeById(flowid, nodeid);

		//request.setField("node",node);
		//request.setField("mindealnum", node.get("mindealnum"));
		
		//this.user.setCache("mindealnum", (String)node.get("mindealnum"));
		
		/*DataList dl = wul.getUserId(flowid, "******", user, node,dutyflag);*/
		DataList dl = wul.getUserId(ServiceWorkflowConst.WF_POSTAUTHORITY_DEAL);
		
		//System.out.println("===========================================mindealnum=" + node.get("mindealnum"));
		/*for(int i=0;i<dl.countRows();i++)
		{
			String[] s = dl.getRow();
			for(int j=0;j<dl.countCols();j++)
				System.out.println("" + dl.getColumn(j) + "==" + s[j]);
		}*/
		ListResult lr = new ListResult(dl);
	//	System.out.println("lr.countRows()=" + lr.countRows());
		return lr;
	}
	
}
