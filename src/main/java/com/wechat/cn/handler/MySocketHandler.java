package com.wechat.cn.handler;

import com.wechat.cn.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/27 17:06
 */
@Slf4j
@Component
public class MySocketHandler extends TextWebSocketHandler {

    /*
        afterConnectionEstablished 方法是在 socket 连接成功后被触发，同原生注解里的 @OnOpen 功能
        afterConnectionClosed  **方法是在 socket 连接关闭后被触发，同原生注解里的 @OnClose 功能
        handleTextMessage **方法是在客户端发送信息时触发，同原生注解里的 @OnMessage 功能
     */


    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineNumber = new AtomicInteger();

    /**
     * 存放客户端连接
     */
    private static ConcurrentHashMap<String, WebSocketSession> sessionPools = new ConcurrentHashMap<>();


    /**
     * 在线人数加一
     */
    public static void addOnlineCount() { onlineNumber.incrementAndGet(); }

    /**
     * 在线人数减一
     */
    public static void subOnlineCount() {
        onlineNumber.decrementAndGet();
    }


    /**
     * 接受客户端消息
     *
     * @param session session
     * @param message message
     * @throws IOException e
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        if (!checkLogin(session,null)){
            return;
        }
        session.sendMessage(new TextMessage(String.format("收到用户：【%s】发来的【%s】",
                session.getAttributes().get("userName"),
                message.getPayload())));
    }

    /**
     * 建立连接后发送消息给客户端
     *
     * @param session session
     * @throws Exception e
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("ws open[{}]",session);
        //需要判断是否登陆
        if (!checkLogin(session,"open")){
            return;
        }

        String userName = session.getAttributes().get("userName").toString();
        WebSocketSession put = sessionPools.put(userName, session);
        if (put == null) {
            addOnlineCount();
        }
        session.sendMessage(new TextMessage("connection to ws succeeded! online number：" + onlineNumber));
    }

    /**
     * 连接关闭后
     *
     * @param session session
     * @param status  status
     * @throws Exception e
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        if (!checkLogin(session,"close")) {
            return;
        }

        Map<String, Object> attributes = session.getAttributes();
        if (attributes.get("userName")!=null){
            String userName = attributes.get("userName").toString();
            sessionPools.remove(userName);
            subOnlineCount();
        }

        log.info("disconnect!");
    }

    /**
     * 发送广播消息
     * @param message 消息内容
     */
    public static void sendTopic(String message) {
        log.info("sendTopic message[{}],sessionPools[{}]",message,sessionPools.size());
        if (sessionPools.isEmpty()) {
            return;
        }
        for (Map.Entry<String, WebSocketSession> entry : sessionPools.entrySet()) {
            try {
                entry.getValue().sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点对点发送消息
     * @param userName     用户
     * @param message 消息
     */
    public static void sendToUser(String userName, String message) {
        WebSocketSession socketSession = sessionPools.get(userName);
        if (socketSession == null) {
            return;
        }
        try {
            socketSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("send to user:{}, error! data:{}", userName, message);
        }
    }

    private Boolean checkLogin(WebSocketSession session,String status){
        if (StringUtils.isEmpty(status)){
            return false;
        }
        if (session.getAttributes().get("type")!=null){
            String type = session.getAttributes().get("type")+"";
            if (StringUtils.isNotEmpty(type) && type.equals("No "+ Constant.Authorization)){
                //没有登陆放入sessionId
                if (status.equals("open")) {
                    sessionPools.put(session.getId(), session);
                }else if (status.equals("close")){
                    sessionPools.remove(session.getId());
                }
                return false;
            }
        }
        return true;
    }
}
