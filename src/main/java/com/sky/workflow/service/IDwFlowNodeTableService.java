package com.sky.workflow.service;

import java.util.List;

import com.sky.core.base.service.IBaseService;
import com.sky.workflow.model.DwFlowNodeTable;

public interface IDwFlowNodeTableService extends IBaseService<DwFlowNodeTable> {

	DwFlowNodeTable getByKey(String flowid, String nodeid);

	List<DwFlowNodeTable> selectByNodeType(String flowid, String nodetype);

}