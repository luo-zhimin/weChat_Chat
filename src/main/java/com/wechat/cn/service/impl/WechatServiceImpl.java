package com.wechat.cn.service.impl;

import com.wechat.cn.service.WechatService;
import com.wechat.cn.util.GsonUtil;
import com.wechat.cn.wechat.WechatAccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.wechat.cn.util.HttpUtil.doPost;

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
        //随机生成一个 scene_str 参数
        String scene_str = "perFei." + System.currentTimeMillis();

        String ticketUrl = String.format("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s",accessToken);

        /*
            参数说明
                expire_seconds	该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
                action_name	二维码类型，QR_SCENE 为临时的整型参数值，QR_STR_SCENE 为临时的字符串参数值，QR_LIMIT_SCENE 为永久的整型参数值，QR_LIMIT_STR_SCENE 为永久的字符串参数值
                action_info	二维码详细信息
                scene_id	场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
                scene_str	场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
         */
        String ticketParam = "{\"expire_seconds\": 3000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + scene_str + "\"}}}";
        String ticketStr = doPost(ticketUrl,ticketParam);
        System.out.println(ticketStr);
        @SuppressWarnings({"all"})
        Map<String, String> ticketMap = GsonUtil.fromJson(ticketStr, Map.class);
        assert ticketMap != null;
        String ticket = ticketMap.get("ticket");
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
    }


}
