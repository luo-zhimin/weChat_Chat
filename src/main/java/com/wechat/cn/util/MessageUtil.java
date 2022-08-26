package com.wechat.cn.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * 解析微信请求Xml
 * @Author : 镜像
 * @create 2022/8/26 18:30
 */
public class MessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * 解析微信发来的请求（XML）
     * @param request
     * @return map
     * @throws Exception
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<String,String>();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            logger.info(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        return map;
    }

    /**
     * 根据消息类型 构造返回消息
     */
    public static String buildXml(Map<String,String> map) {
        String result;
        String msgType = map.get("MsgType");
        if(msgType.equalsIgnoreCase("TEXT")){
            result = buildTextMessage(map, "亲，有什么可以帮到您的?");
        }else{
            String fromUserName = map.get("FromUserName");
            // 开发者微信号
            String toUserName = map.get("ToUserName");
            String alert = map.get("alert");
            result = String
                    .format(
                            "<xml>" +
                                    "<ToUserName><![CDATA[%s]]></ToUserName>" +
                                    "<FromUserName><![CDATA[%s]]></FromUserName>" +
                                    "<CreateTime>%s</CreateTime>" +
                                    "<MsgType><![CDATA[text]]></MsgType>" +
                                    "<Content><![CDATA[%s]]></Content>" +
                                    "</xml>",
                            fromUserName, toUserName, getUtcTime(),
                            alert);
//        "请回复如下关键词：\n文本\n图片\n语音\n视频\n音乐\n图文");
        }
        return result;
    }

    /**
     * 构造文本消息
     */
    private static String buildTextMessage(Map<String,String> map, String content) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /*
          文本消息XML数据格式
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" + "</xml>",
                fromUserName, toUserName, getUtcTime(), content);
    }

    private static String getUtcTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception ignored) {}
        return String.valueOf(dd);
    }
}
