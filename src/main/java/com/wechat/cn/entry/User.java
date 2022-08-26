package com.wechat.cn.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;


@Data
@ApiModel(value = "用户")
@Builder
public class User {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "真实姓名")
    private String name;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime updateTime;

    @Tolerate
    public User(){}
}
