package com.sky.workflow.service;

import com.sky.core.base.service.IBaseService;
import com.sky.workflow.model.DwTaskTable;
import com.sky.workflow.model.DwTaskTableKey;

public interface IDwTaskTableService extends IBaseService<DwTaskTable> {

	DwTaskTable getByWfidUser(String wfid, String bankid, String operid);

	void deleteByPrimaryKey(DwTaskTableKey key);

	int getNumByWfidNodeId(String wfid, String nodeid);

}