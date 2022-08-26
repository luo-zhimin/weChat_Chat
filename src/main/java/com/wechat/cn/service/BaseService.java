package com.wechat.cn.service;

import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/26 22:25
 */
@Service
@Slf4j
public abstract class BaseService {

    private final Long expireTime = System.currentTimeMillis() + 24 * 60 * 60;

    public String createToken(String userName){
        HashMap<String, Object> tokenMap = new HashMap<String, Object>(2) {
            {
                put("userName", userName);
                put("expireTime", expireTime);
            }
        };
        return JWTUtil.createToken(tokenMap, userName.getBytes(StandardCharsets.UTF_8));
    }


}
