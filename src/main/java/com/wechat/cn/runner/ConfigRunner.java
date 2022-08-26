package com.wechat.cn.runner;

import com.wechat.cn.service.impl.ConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * 启动加载 可以放到缓冲
 * @Author : 镜像
 * @create 2022/8/25 22:28
 */
@Component
public class ConfigRunner implements CommandLineRunner {

    @Autowired
    private ConfigServiceImpl service;

    @Override
    public void run(String... args) throws Exception {
        this.service.getConfigMap();
    }
}
