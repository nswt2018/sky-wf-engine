package com.sky.workflow.dao;

import java.util.List;

import com.sky.workflow.model.DwFlowNodeTable;

import tk.mybatis.mapper.common.Mapper;

public interface DwFlowNodeTableDao extends Mapper<DwFlowNodeTable> {
	
	List<DwFlowNodeTable> selectByNodeType(String flowid, String nodetype);
	
}
