package com.wechat.cn.entry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
* Created by IntelliJ IDEA.
* @Author : 镜像
* @create 2022/8/25 21:11
*/
@ApiModel(value="config")
@Data
@ToString
public class Config {
    @ApiModelProperty(value="")
    private Long id;

    @ApiModelProperty(value="")
    private String configKey;

    @ApiModelProperty(value="")
    private String configValue;

    @ApiModelProperty(value="")
    private String remark;
}