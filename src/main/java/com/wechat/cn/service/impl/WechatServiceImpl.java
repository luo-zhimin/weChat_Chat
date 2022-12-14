package com.wechat.cn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.cn.dao.UserMapper;
import com.wechat.cn.dao.WechatUserMapper;
import com.wechat.cn.entry.User;
import com.wechat.cn.entry.WechatUser;
import com.wechat.cn.exception.ServiceException;
import com.wechat.cn.handler.MySocketHandler;
import com.wechat.cn.response.Login;
import com.wechat.cn.service.BaseService;
import com.wechat.cn.service.WechatService;
import com.wechat.cn.util.GsonUtil;
import com.wechat.cn.util.MessageUtil;
import com.wechat.cn.util.SHA1;
import com.wechat.cn.wechat.WechatAccessTokenService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wechat.cn.util.HttpUtil.doPost;

/**
 * Created by IntelliJ IDEA.
 * @Author : éċ
 * @create 2022/8/25 22:17
 */
@Service
@Slf4j
public class WechatServiceImpl extends BaseService implements WechatService {

    private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private WechatAccessTokenService accessTokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WechatUserMapper wechatUserMapper;


    @Override
    public String getLoginQCode() {
        String accessToken = accessTokenService.getAccessToken();
        //éĉşçĉä¸ä¸Ş scene_str ċĉ°
        String scene_str = "perFei." + System.currentTimeMillis();

        //todo ĉ ıĉ?ä¸ċç scene_str çĉä¸ċç äşçğ´ç  ċ¤çä¸ċçéğè?  ċ??äżĦċè° EventKey <==> scene_str
        String ticketUrl = String.format("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s",accessToken);

        /*
            ċĉ°èŻ´ĉ
                expire_seconds	èŻ?äşçğ´ç ĉĉĉĥé´ïĵäğ?ç§ä¸şċä½? ĉċ¤§ä¸èĥèż2592000ïĵċ³30ċ¤İïĵïĵĉ­¤ċ­ĉ?µċĤĉä¸ċĦĞïĵċéğè?¤ĉĉĉä¸ş30ç§?
                action_name	äşçğ´ç çħğċïĵQR_SCENE ä¸şä¸´ĉĥçĉ´ċċĉ°ċĵïĵQR_STR_SCENE ä¸şä¸´ĉĥçċ­çĴĤä¸²ċĉ°ċĵïĵQR_LIMIT_SCENE ä¸şĉ°¸äıçĉ´ċċĉ°ċĵïĵQR_LIMIT_STR_SCENE ä¸şĉ°¸äıçċ­çĴĤä¸²ċĉ°ċĵ
                action_info	äşçğ´ç èŻĤçğäżĦĉŻ
                scene_id	ċşĉŻċĵIDïĵä¸´ĉĥäşçğ´ç ĉĥä¸ş32ä½é0ĉ´ċïĵĉ°¸äıäşçğ´ç ĉĥĉċ¤§ċĵä¸ş100000ïĵç?ċċĉ°ċŞĉŻĉ1--100000ïĵ
                scene_str	ċşĉŻċĵIDïĵċ­çĴĤä¸²ċ½˘ċĵçIDïĵïĵċ­çĴĤä¸²çħğċïĵéżċşĤéċĥä¸ş1ċ°64
         */

        String ticketParam = "{\"expire_seconds\": 3000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + scene_str + "\"}}}";
        String ticketStr = doPost(ticketUrl,ticketParam);
        @SuppressWarnings({"all"})
        Map<String, String> ticketMap = GsonUtil.fromJson(ticketStr, Map.class);
        assert ticketMap != null;
        String ticket = ticketMap.get("ticket");
        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
    }

    @Override
    public void verifyGet(HttpServletRequest request, HttpServletResponse response) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        //èżéċĦĞċşĉĴéç½?ä¸­çtoken
        String token = ConfigServiceImpl.configMap.get("token");
        String p = SHA1.getSHA1(token, timestamp, nonce,"");
        logger.info("ċ ċŻ:{}",p);
        logger.info("ĉĴèşĞ:{}", signature);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (p.equals(signature)){
                logger.info("éŞèŻĉċ");
                out.print(echostr);
            }else {
                logger.info("éŞèŻċ¤ħè´?");
            }
        } catch (IOException e) {
            logger.info("éŞèŻċ¤ħè´?");
            logger.error("", e);
        }
    }

    @Override
    @Transactional
    @SneakyThrows
    public void verifyPost(HttpServletRequest request, HttpServletResponse response) {
        // ĉ?ĉĥ?ċ¤ç?ċċşçħċ??äżĦĉċĦċ¨è½Ĵċçç¨ĉ·ċéçğċĴäĵċ·çĉĥĉŻ
        // ċ°èŻ·ĉħ?ċċşççĵç ċè??ç½?ä¸şUTF-8ïĵé²ĉ­˘ä¸­ĉäıħç ïĵ
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String result = "";
        try {
            //è·ċ?è§£ĉċ??äżĦċĉ?çèŻ·ĉħ
            Map<String,String> map = MessageUtil.parseXml(request);
            // éèżopenidè·ċç¨ĉ·äżĦĉŻ
            String openId = map.get("FromUserName");
            String event = map.get("EventKey");
            logger.info("event:"+event+"-openId:"+openId);
            if (StringUtils.isNotEmpty(event)) {
                //todo ċçğ­ĉċħ
                if (event.contains("perFei")) {
                    // çğé
                    // redisĉevent ċ openIdċ­ċ¨èµ·ĉ?
                    redisTemplate.opsForValue().set(event, openId);
                    redisTemplate.expire(event, 3000, TimeUnit.SECONDS);

                    boolean live = wechatUserMapper.hasLiveByOpenId(openId);
                    if (live) {
                        map.put("alert", "çğċ½ĉċ");
//                        MySocketHandler.sendTopic("ç¨ĉ·çğċ½ĉċ");
                    } else {
                        //ċċğşç¨ĉ·
                        User user = User.builder()
                                .userName(RandomStringUtils.randomNumeric(10))
                                .password(RandomStringUtils.randomNumeric(10))
                                .isDelete(Boolean.FALSE)
                                .createTime(LocalDateTime.now())
                                .build();
                        userMapper.insertSelective(user);

                        WechatUser wechatUser = WechatUser.builder()
                                .relationId(user.getId())
                                .openId(openId)
                                .isDel(Boolean.FALSE)
                                .userType(0)
                                .createTime(LocalDateTime.now())
                                .build();
                        this.wechatUserMapper.insertSelective(wechatUser);
                        map.put("alert", "çğċ?ĉċ");
                    }
                }

                //è°ċws ċéäżĦĉŻ éç?ċçĞŻçğéĉċ
                wxScanLogin(event);
            }

            if (StringUtils.isNotEmpty(openId)){
                //ĉċä¸ĉ´ĉ°
                boolean hasUserInfo = wechatUserMapper.hasUserInfoByOpenId(openId);
                if (hasUserInfo) {
                    //è·ċċĴäĵċ·çç¨ĉ·äżĦĉŻ
                    String weChatUserInfo = this.accessTokenService.getWeChatUserInfo(openId);
                    JSONObject jsonObject = JSONObject.parseObject(weChatUserInfo);
//                    logger.info("äżċ­ç¨ĉ·ä¸ŞäşşäżĦĉŻ [{}]", jsonObject);
                    this.wechatUserMapper.updateUserInfoByOpenId(openId, jsonObject.get("unionid") + "", weChatUserInfo);
                }
            }

            //ĉ ıĉ?ĉĥĉŻçħğċ ĉé èżċĉĥĉŻ
            result = MessageUtil.buildXml(map);
            if(result.equals("")){
                result = "ĉŞĉ­£çĦ?ċċş";
            }
        } catch (Exception e) {
            logger.error("ċçċĵċ¸¸ïĵ"+ e.getMessage());
            e.printStackTrace();
        }
        response.getWriter().println(result);
    }

    @Override
    public Login wxScanLogin(String sceneStr) {

        String openId = redisTemplate.opsForValue().get(sceneStr);
        if (StringUtils.isEmpty(openId)){
            return null;
        }

        boolean live = wechatUserMapper.hasLiveByOpenId(openId);

        if (!live){
            throw new ServiceException(1000,"èŻ·çğċ?ç¨ĉ·");
        }

        Login build = Login.builder()
                .userName(openId)
                .token(createToken(openId))
                .build();

        logger.info("ç¨ĉ·ĉĞç çğċ½ĉċ");
        MySocketHandler.sendTopic(JSON.toJSONString(build));
        return build;
    }
}
