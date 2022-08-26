package com.wechat.cn.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.cn.service.impl.ConfigServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    //检查token是否在效期内
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

    @SneakyThrows
    private String doGet(String url){
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        return EntityUtils.toString(entity);
    }
}
