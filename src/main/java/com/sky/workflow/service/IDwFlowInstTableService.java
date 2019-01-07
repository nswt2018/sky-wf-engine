package com.sky.workflow.service;

import com.sky.core.base.service.IBaseService;
import com.sky.workflow.model.DwFlowInstTable;

public interface IDwFlowInstTableService extends IBaseService<DwFlowInstTable> {

	void updateByPrimaryKeySelective(DwFlowInstTable dwFlowInstVo);

}