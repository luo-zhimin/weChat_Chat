package com.wechat.cn.service.impl;

import com.wechat.cn.dao.ConfigMapper;
import com.wechat.cn.entry.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/25 22:23
 */
@Service
@Slf4j
public class ConfigServiceImpl {

    @Autowired
    private ConfigMapper configMapper;

    public static ConcurrentMap<String,String> configMap = new ConcurrentHashMap<>();

    public void getConfigMap(){
        configMap = this.configMapper.getConfigs().stream()
                .collect(Collectors.toConcurrentMap(Config::getConfigKey,Config::getConfigValue));
    }
}
