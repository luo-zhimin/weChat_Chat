package com.wechat.cn.controller;

import com.wechat.cn.entry.User;
import com.wechat.cn.service.UserService;
import com.wechat.cn.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/27 16:05
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @ApiOperation(value = "获取当前用户信息")
    public Result<?> getUserInfo(){
        return Result.success(userService.getUserInfo());
    }

    @PostMapping("/update")
    @ApiOperation(value = "用户修改信息")
    public Result<?> updateUserInfo(@RequestBody User user){
        return Result.success(userService.updateUserInfo(user));
    }
}
