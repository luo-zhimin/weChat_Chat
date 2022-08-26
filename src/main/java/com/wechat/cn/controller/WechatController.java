package com.wechat.cn.controller;

import com.wechat.cn.service.WechatService;
import com.wechat.cn.util.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<?> getLoginQCode(){
        return Result.success(wechatService.getLoginQCode());
    }


//    @RequestMapping("getOpenId")
//    public ResultJson getOpenId(String eventKey){
//        if(loginMap.get(eventKey) == null){
//            return ResultJson.error("未扫码成功！") ;
//        }
//        CodeLoginKey codeLoginKey = loginMap.get(eventKey);
//        String openId = codeLoginKey.getOpenId();
//        loginMap.remove(eventKey);
//        return ResultJson.ok(codeLoginKey);
//    }


}
