package com.sky.workflow.dao;

import com.sky.workflow.model.DbBusiWfMapTable;

import tk.mybatis.mapper.common.Mapper;

public interface DbBusiWfMapTableDao extends Mapper<DbBusiWfMapTable> {
    int deleteByPrimaryKey(String wfid);

    int insert(DbBusiWfMapTable record);

    int insertSelective(DbBusiWfMapTable record);

    DbBusiWfMapTable selectByPrimaryKey(String wfid);

    int updateByPrimaryKeySelective(DbBusiWfMapTable record);

    int updateByPrimaryKey(DbBusiWfMapTable record);
}