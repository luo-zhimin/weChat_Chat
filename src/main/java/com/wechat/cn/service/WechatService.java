package com.wechat.cn.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/25 22:16
 */
public interface WechatService {
    String getLoginQCode();

    void verifyGet(HttpServletRequest request, HttpServletResponse response);

    void verifyPost(HttpServletRequest request, HttpServletResponse response);

    Object wxScanLogin(String sceneStr);
}
