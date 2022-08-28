package com.wechat.cn.handler;

import com.alibaba.fastjson.JSON;
import com.wechat.cn.constant.Constant;
import com.wechat.cn.response.ReceiveMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
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
//        if (!checkLogin(session,null)){
//            log.info("需要登陆才可以发送信息");
//            return;
//        }
        String userName = session.getAttributes().get("userName") + "";
        if (StringUtils.isEmpty(userName)) {
            userName = "default";
        }
        session.sendMessage(new TextMessage(String.format("收到用户：【%s】发来的【%s】",
                userName,
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
        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
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
     * 消息处理
     * @param session session
     * @param message 消息
     * @throws Exception 异常
     */
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("accept handle message");
        String receiveMessage = message.getPayload() + "";
        //登陆之后发送
        String userName = session.getAttributes().get("userName") + "";
        //解析内容...
        ReceiveMessage userMessage = JSON.parseObject(receiveMessage, ReceiveMessage.class);
        String msg = userMessage.getMessage();
        String type = userMessage.getType();
        log.info("accept message[{}]",userMessage);
        //...
    }

    /**
     * 用户连接错误
     * @param session 会话session
     * @param exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("服务端发生了错误: "+exception.getMessage());
        closeConnection(session);
    }

    /**
     * socket 连接执行
     */
    @Override
    public boolean supportsPartialMessages() {
        log.debug("Method for supportsPartialMessages");
        return super.supportsPartialMessages();
    }

    private void closeConnection(WebSocketSession webSocketSession) {
        String sessionId = webSocketSession.getId();

        checkLogin(webSocketSession,"close");
        log.info("有连接关闭！ sessionId：{}，", sessionId);
        log.info("在线人数{}",onlineNumber);
    }


    /**
     * 发送广播消息
     * @param message 消息内容
     * @return 是否成功发送
     */
    public static Boolean sendTopic(String message) {
        log.info("sendTopic message[{}],sessionPools[{}]",message,sessionPools.size());
        if (sessionPools.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, WebSocketSession> entry : sessionPools.entrySet()) {
            try {
                //是否给在线发送或者全部
//                if (entry.getValue().isOpen()){
//                    entry.getValue().sendMessage(new TextMessage(message));
//                    return true;
//                }
                entry.getValue().sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 点对点发送消息
     * @param userName     用户
     * @param message 消息
     * @return 是否成功发送
     */
    public static Boolean sendToUser(String userName, String message) {
        WebSocketSession socketSession = sessionPools.get(userName);
        if (socketSession == null) {
            //离线处理......待处理
            return false;
        }
        try {
            socketSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("send to user:{}, error! data:{}", userName, message);
        }
        return true;
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

//    private void handleType(WebSocketSession session){
//        if (session.getAttributes().get("type")!=null){
//            String type = session.getAttributes().get("type")+"";
//            if (StringUtils.isNotEmpty(type) && type.equals("No "+ Constant.Authorization)){
//                //没有登陆放入sessionId
//                if (status.equals("open")) {
//                    sessionPools.put(session.getId(), session);
//                }else if (status.equals("close")){
//                    sessionPools.remove(session.getId());
//                }
//                return false;
//            }
//        }
//    }
}
