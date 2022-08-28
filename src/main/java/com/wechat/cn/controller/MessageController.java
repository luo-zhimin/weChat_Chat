package com.wechat.cn.controller;

import com.wechat.cn.request.SendMessageRequest;
import com.wechat.cn.service.MessageService;
import com.wechat.cn.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/28 12:30
 */
@RestController
@RequestMapping("/message")
@Api(tags = "信息管理")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendTopic")
    @ApiOperation(value = "发送广播信息")
    public Result<?> sendTopic(@RequestBody SendMessageRequest request){
        return Result.success(messageService.sendTopic(request));
    }

    @PostMapping("/sendToUser")
    @ApiOperation(value = "给指定用户发送信息")
    public Result<?> sendToUser(@RequestBody SendMessageRequest request){
        return Result.success(messageService.sendToUser(request));
    }
}
