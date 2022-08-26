package com.wechat.cn.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/26 21:39
 */
@Data
@ToString
@Builder
public class Login {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "token")
    private String token;

    @Tolerate
    public Login(){}
}
