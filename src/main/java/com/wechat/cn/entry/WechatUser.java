package com.wechat.cn.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
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
@Builder
public class WechatUser {
    @ApiModelProperty(value="")
    private Long id;

    @ApiModelProperty(value="open Id")
    private String openId;

    @ApiModelProperty(value="关联id")
    private String unionId;

    @ApiModelProperty(value="用户信息")
    private String userInfo;

    @ApiModelProperty(value="是否删除")
    private Boolean isDel;

    @ApiModelProperty(value="用户类型 1是user")
    private Integer userType;

    @ApiModelProperty(value="关系Id")
    private Long relationId;

    @ApiModelProperty(value="创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;

    @Tolerate
    public WechatUser(){}
}