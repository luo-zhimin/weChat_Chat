package com.wechat.cn.controller;

import com.wechat.cn.service.WechatService;
import com.wechat.cn.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/25 22:15
 */
@RestController
@RequestMapping("/wechat")
@Api(tags = "微信管理")
public class WechatController {

    @Autowired
    private WechatService wechatService;

    @GetMapping("/getLoginQCode")
    @ApiOperation(value = "获取扫码二维码地址")
    public Result<?> getLoginQCode(){
        return Result.success(wechatService.getLoginQCode());
    }

    @GetMapping("verify")
    @ApiOperation(value = "微信公众号验证")
    public void verifyGet(HttpServletRequest request, HttpServletResponse response) {
        wechatService.verifyGet(request,response);
    }

    @PostMapping("verify")
    @ApiOperation(value = "微信公众号回调")
    public void verifyPost(HttpServletRequest request, HttpServletResponse response) {
        wechatService.verifyPost(request,response);
    }

    @ApiOperation(value = "用户扫码登录--临时二维码")
    @GetMapping("/wxScanLogin")
    public Result<?> wxScanLogin(@RequestParam(value = "scene_str",defaultValue = "") String sceneStr) {
        return Result.success(wechatService.wxScanLogin(sceneStr));
    }
}
