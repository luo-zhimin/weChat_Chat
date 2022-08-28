package com.wechat.cn.service.impl;

import com.wechat.cn.handler.MySocketHandler;
import com.wechat.cn.request.SendMessageRequest;
import com.wechat.cn.service.BaseService;
import com.wechat.cn.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/28 12:32
 */
@Service
public class MessageServiceImpl extends BaseService implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);


    @Override
    public Boolean sendTopic(SendMessageRequest request) {
        logger.info("sendTopic [{}]",request);
        return MySocketHandler.sendTopic(request.getMessage());
    }

    @Override
    public Boolean sendToUser(SendMessageRequest request) {
        logger.info("sendToUser [{}]",request);
        return MySocketHandler.sendToUser(request.getUserName(),request.getMessage());
    }
}
