package com.wechat.cn.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
* Created by IntelliJ IDEA.
* @Author : 镜像
* @create 2022/8/25 21:11
*/
@ApiModel(value="wechat_user")
@Data
@ToString
public class WechatUser {
    @ApiModelProperty(value="")
    private Long id;

    @ApiModelProperty(value="open Id")
    private String openId;

    @ApiModelProperty(value="")
    private String unionId;

    @ApiModelProperty(value="用户信息")
    private String userInfo;

    @ApiModelProperty(value="是否删除")
    private Integer isDel;

    @ApiModelProperty(value="")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;
}