package com.springboot.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.springboot.entity.SendEpLog;

@Mapper
public interface SendEpLogDao {
	int deleteByPrimaryKey(Integer id);

	void insert(SendEpLog record);

	int insertSelective(SendEpLog record);

	SendEpLog selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SendEpLog record);

	int updateByPrimaryKey(SendEpLog record);

	SendEpLog findInfoLog(Map<String, Object> map);
}