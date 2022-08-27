package com.wechat.cn.service;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.wechat.cn.constant.Constant;
import com.wechat.cn.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 根据用户名生成token
     * @param userName 用户名
     * @return token
     */
    public String createToken(String userName){
        HashMap<String, Object> tokenMap = new HashMap<String, Object>(2) {
            {
                put("userName", userName);
                put("expireTime", expireTime);
            }
        };
        return JWTUtil.createToken(tokenMap, userName.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析header token 获取当前登陆用户信息
     * @return 用户名
     */
    public String getCurrentUser(){
        String authorization = getRequest().getHeader(Constant.Authorization);
        if (StringUtils.isEmpty(authorization)){
            throw new ServiceException(403,"请重新登陆");
        }
        JWT jwt = JWTUtil.parseToken(authorization);
        JWTPayload payload = jwt.getPayload();
        return payload.getClaim("userName")+"";
    }


    private HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }
}
