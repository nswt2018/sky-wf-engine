package com.sky.workflow.service.impl;

import org.springframework.stereotype.Service;

import com.sky.core.base.service.impl.BaseServiceImpl;
import com.sky.workflow.model.DwFlowInstTable;
import com.sky.workflow.service.IDwFlowInstTableService;

import tk.mybatis.mapper.common.Mapper;

@Service("DwFlowInstTableService")
public class DwFlowInstTableServiceImpl extends BaseServiceImpl<DwFlowInstTable> implements IDwFlowInstTableService {

	@Override
	public void updateByPrimaryKeySelective(DwFlowInstTable dwFlowInstVo) {
		Mapper<DwFlowInstTable> mapper = super.getDao();
		mapper.updateByPrimaryKeySelective(dwFlowInstVo);
	}
	
}
