package com.sky.workflow.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sky.core.base.service.impl.BaseServiceImpl;
import com.sky.workflow.model.DwTaskTable;
import com.sky.workflow.model.DwTaskTableKey;
import com.sky.workflow.service.IDwTaskTableService;

import tk.mybatis.mapper.common.Mapper;

@Service("DwTaskTableService")
public class DwTaskTableServiceImpl extends BaseServiceImpl<DwTaskTable> implements IDwTaskTableService {

	@Override
	public DwTaskTable getByWfidUser(String wfid, String bankid, String operid) {
		DwTaskTable dwTaskVo = new DwTaskTable();
		dwTaskVo.setWfid(wfid);
		dwTaskVo.setBankid(bankid);
		dwTaskVo.setOperid(operid);
		Mapper<DwTaskTable> mapper = super.getDao();
		List<DwTaskTable> rsList = mapper.select(dwTaskVo);
		return rsList != null ? rsList.get(0) : null;
	}

	@Override
	public void deleteByPrimaryKey(DwTaskTableKey key) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumByWfidNodeId(String wfid, String nodeid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
