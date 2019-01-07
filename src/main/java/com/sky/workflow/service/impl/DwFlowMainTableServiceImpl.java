package com.sky.workflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.core.base.service.impl.BaseServiceImpl;
import com.sky.workflow.model.DwFlowMainTable;
import com.sky.workflow.service.IDwFlowMainTableService;

@Service("DwFlowMainTableService")
@Transactional
public class DwFlowMainTableServiceImpl extends BaseServiceImpl<DwFlowMainTable> implements IDwFlowMainTableService {

}
