package com.wechat.cn.request;

import lombok.Data;
import lombok.ToString;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/28 12:34
 */
@Data
@ToString
public class SendMessageRequest {

    private String message;

    private String userName;
}
