package com.wechat.cn.service;

import com.wechat.cn.request.SendMessageRequest;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/28 12:31
 */
public interface MessageService {


    Boolean sendTopic(SendMessageRequest request);

    Boolean sendToUser(SendMessageRequest request);
}
