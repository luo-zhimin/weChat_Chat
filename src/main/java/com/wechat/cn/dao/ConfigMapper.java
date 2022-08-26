package com.wechat.cn.dao;

import com.wechat.cn.entry.Config;

import java.util.List;

/**
* Created by IntelliJ IDEA.
* @Author : 镜像
* @create 2022/8/25 21:11
*/
public interface ConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKey(Config record);

    List<Config> getConfigs();
}