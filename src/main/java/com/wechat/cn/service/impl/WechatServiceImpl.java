package com.wechat.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wechat.cn.dao.UserMapper;
import com.wechat.cn.dao.WechatUserMapper;
import com.wechat.cn.entry.User;
import com.wechat.cn.entry.WechatUser;
import com.wechat.cn.exception.ServiceException;
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
 * @Author : 镜像
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
        //随机生成一个 scene_str 参数
        String scene_str = "perFei." + System.currentTimeMillis();

        //todo 根据不同的 scene_str 生成不同的 二维码 处理不同的逻辑  微信回调 EventKey <==> scene_str
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
        //这里填基本配置中的token
        String token = ConfigServiceImpl.configMap.get("token");
        String p = SHA1.getSHA1(token, timestamp, nonce,"");
        logger.info("加密:{}",p);
        logger.info("本身:{}", signature);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (p.equals(signature)){
                logger.info("验证成功");
                out.print(echostr);
            }else {
                logger.info("验证失败");
            }
        } catch (IOException e) {
            logger.info("验证失败");
            logger.error("", e);
        }
    }

    @Override
    @Transactional
    @SneakyThrows
    public void verifyPost(HttpServletRequest request, HttpServletResponse response) {
        // 接收、处理、响应由微信服务器转发的用户发送给公众号的消息
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String result = "";
        try {
            //获得解析微信发来的请求
            Map<String,String> map = MessageUtil.parseXml(request);
            // 通过openid获取用户信息
            String openId = map.get("FromUserName");
            String event = map.get("EventKey");
            logger.info("event:"+event+"-openId:"+openId);
            if (StringUtils.isNotEmpty(event)) {
                //todo 后续拓展
                if (event.contains("perFei")) {
                    // 登錄
                    // redis把event 和 openId存储起来
                    redisTemplate.opsForValue().set(event, openId);
                    redisTemplate.expire(event, 3000, TimeUnit.SECONDS);

                    boolean live = wechatUserMapper.hasLiveByOpenId(openId);
                    if (live) {
                        map.put("alert", "登录成功");
                    } else {
                        //创建用户
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
                        map.put("alert", "绑定成功");
//                        map.put("alert", "账号未绑定");
                    }
                }
            }

            if (StringUtils.isNotEmpty(openId)){
                //获取公众号的用户信息
                String weChatUserInfo = this.accessTokenService.getWeChatUserInfo(openId);
                JSONObject jsonObject = JSONObject.parseObject(weChatUserInfo);
                logger.info("保存用户个人信息 [{}]",jsonObject);
                this.wechatUserMapper.updateUserInfoByOpenId(openId,jsonObject.get("unionid")+"",weChatUserInfo);
            }
            //根据消息类型 构造返回消息
            result = MessageUtil.buildXml(map);
            if(result.equals("")){
                result = "未正确响应";
            }
        } catch (Exception e) {
            logger.error("发生异常："+ e.getMessage());
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
            throw new ServiceException(1000,"请绑定用户");
        }

        Login build = Login.builder()
                .userName(openId)
                .token(createToken(openId))
                .build();

        logger.info("企业用户扫码登录成功");
        return build;
    }
}
