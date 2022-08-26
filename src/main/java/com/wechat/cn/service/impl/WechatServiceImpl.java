package com.wechat.cn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wechat.cn.service.WechatService;
import com.wechat.cn.util.GsonUtil;
import com.wechat.cn.wechat.WechatAccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/25 22:17
 */
@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WechatAccessTokenService accessTokenService;


    @Override
    public String getLoginQCode() {
        String accessToken = accessTokenService.getAccessToken();
        String scene_str = "perFei." + System.currentTimeMillis();

        String getTicketUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken+ "";
        // 临时整形参数值
        String ticketParam = "{\"expire_seconds\": 120, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + scene_str + "\"}}}";
        String ticketStr = doPost(getTicketUrl,ticketParam);
        System.out.println(ticketStr);
        @SuppressWarnings({"all"})
        Map<String, String> ticketMap = GsonUtil.fromJson(ticketStr, Map.class);
        assert ticketMap != null;
        String ticket = ticketMap.get("ticket");
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
    }

    public static String doPost(String url, String jsonData) {
        return doPost(url, jsonData, StandardCharsets.UTF_8);
    }
    public static String doPost(String url, String jsonData, Charset charset) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(jsonData, "utf-8");
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String r = EntityUtils.toString(entity, charset);
            log.info("result: {}", r);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
