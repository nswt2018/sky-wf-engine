package com.sky.workflow.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sky.core.base.service.impl.BaseServiceImpl;
import com.sky.workflow.model.DwFlowNodeTable;
import com.sky.workflow.service.IDwFlowNodeTableService;

import tk.mybatis.mapper.common.Mapper;

@Service("DwFlowNodeTableService")
public class DwFlowNodeTableServiceImpl extends BaseServiceImpl<DwFlowNodeTable> implements IDwFlowNodeTableService {

	@Override
	public DwFlowNodeTable getByKey(String flowid, String nodeid) {

		DwFlowNodeTable dwFlowNodeVo = new DwFlowNodeTable();
		dwFlowNodeVo.setFlowid(flowid);
		dwFlowNodeVo.setNodeid(nodeid);
		Mapper<DwFlowNodeTable> mapper = super.getDao();
		DwFlowNodeTable record = mapper.selectOne(dwFlowNodeVo);

		return record;
	}

	@Override
	public List<DwFlowNodeTable> selectByNodeType(String flowid, String nodetype) {

		DwFlowNodeTable dwFlowNodeVo = new DwFlowNodeTable();
		dwFlowNodeVo.setFlowid(flowid);
		dwFlowNodeVo.setNodetype(nodetype);
		Mapper<DwFlowNodeTable> mapper = super.getDao();
		List<DwFlowNodeTable> rsList = mapper.select(dwFlowNodeVo);

		return rsList;
	}

}
