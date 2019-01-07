package com.sky.workflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.core.base.service.impl.BaseServiceImpl;
import com.sky.workflow.model.DbBusiWfMapTable;
import com.sky.workflow.service.IDbBusiWfMapTableService;

@Service("DbBusiWfMapTableService")
@Transactional
public class DbBusiWfMapTableServiceImpl extends BaseServiceImpl<DbBusiWfMapTable> implements IDbBusiWfMapTableService {

}
