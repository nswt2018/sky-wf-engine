package com.nswt.workflow.service;

import java.util.HashMap;
import java.util.Map;

import cn.com.jbbis.afx.AppResponse;
import cn.com.jbbis.afx.ListResult;
import cn.com.jbbis.common.BaseService;
import cn.com.jbbis.util.DataList;

public class ServiceDwFlowNodeSelect extends BaseService{

	@Override
	protected AppResponse process() throws Exception {
		Map<Object,Object> params=new HashMap<Object,Object>();
		String tpl = context.getString("procedure");
		//查询所有节点信息
		DataList dataList = this.executeProcedure(tpl);
		DataList dl =new DataList();
		// 设置列名
		String colsname[] = dataList.getColumns();
		for (int k = 0; k < colsname.length; k++) {
			dl.addColumn(colsname[k]);
		}
		dl.addColumn("rolename");
		//根据查询出的节点信息的nodeid查询每个节点的角色信息
		params.put("flowid",request.getField("flowid"));
		int i=0;
		String nodeid="";
		int nodeidNum=dataList.getColumnIndex("nodeid");
		while(dataList.next()){
			dl.addRow();
			//插入每一个数据
			for (int l = 0; l < colsname.length; l++) {
				String name = dataList.getColumn(l);
				String value = dataList.getString(name);
				dl.setCell(name, value);
			}
			//获取nodeid查询角色信息
			nodeid=dataList.getRow(i)[nodeidNum];
			params.put("nodeid",nodeid);
			DataList dl1 = this.executeProcedure("wf.SelectRoleNameByNodeid", params);
			String rolename="";
			int j=0;
			while(dl1.next()){
				rolename=rolename+dl1.getRow(j)[0]+"," ;
				j++;
			}
			if(!"".equals(rolename)){
				rolename=rolename.substring(0, rolename.length()-1);
			}
			dl.setCell("rolename", rolename);
			i++;
		}
		
		return new ListResult(dl);
	}

}
