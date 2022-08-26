package com.wechat.cn.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.cn.service.impl.ConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.wechat.cn.util.HttpUtil.doGet;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/25 22:35
 */
@Service
@Slf4j
public class WechatAccessTokenService {

    private volatile String accessToken;

    private volatile String weChatUserInfo;

    /**
     * @return 获取AccessToken
     */
    public synchronized String getAccessToken() {
        getAccessTokenAndRefresh();
        return accessToken;
    }

    public synchronized String getWeChatUserInfo(String openId) {
        getUserInfo(openId);
        return weChatUserInfo;
    }

    public void refreshAccessToken() {
        getAccessTokenAndRefresh();
    }

    /**
     * 检查token是否在效期内
     * @param refreshTime 刷新时间
     * @return 是否过期
     */
    private boolean checkTokenValid(Date refreshTime) {
        Date now = new Date();
        return refreshTime.getTime() + 7000 * 1000 > now.getTime();
    }

    private void getAccessTokenAndRefresh() {
        String wechatAppId = ConfigServiceImpl.configMap.get("wechat_appid");
        String wechatSecret = ConfigServiceImpl.configMap.get("wechat_secret");
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                wechatAppId, wechatSecret);
        log.info("【公众号】获取accessToken url:{}", url);

        String r = doGet(url);
        log.info("【公众号】获取accessToken结果:{}", r);
        JSONObject json = JSON.parseObject(r);
        this.accessToken = json.getString("access_token");
    }

    private void getUserInfo(String openId) {
        String url = String.format("https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s",
                this.getAccessToken(), openId);
        log.info("【公众号】获取关注的个人信息 url:{}", url);
        String result = doGet(url);
        log.info("【公众号】获取关注的个人信息结果:{}", result);
        this.weChatUserInfo = result;
    }
}
