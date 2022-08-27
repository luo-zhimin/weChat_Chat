package com.wechat.cn.interceptor;

import cn.hutool.jwt.JWTUtil;
import com.wechat.cn.constant.Constant;
import com.wechat.cn.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : 镜像
 * @create 2022/8/27 16:59
 */
@Component
public class WebSocketInterceptor extends BaseService implements HandshakeInterceptor {

    private final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    /**
     * 握手之前
     * @param serverHttpRequest request
     * @param serverHttpResponse response
     * @param webSocketHandler handler
     * @param map 参数
     * @return 是否成功
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler,
                                   Map<String, Object> map) throws Exception
    {
        ServletServerHttpRequest httpRequest = (ServletServerHttpRequest) serverHttpRequest;
        ServletServerHttpResponse httpResponse = (ServletServerHttpResponse) serverHttpResponse;

        logger.info("beforeHandshake url[{}]",httpRequest.getURI());
        String authorization = httpRequest.getServletRequest().getHeader(Constant.Authorization);
        //未登录
        if (StringUtils.isEmpty(authorization)){
            String type = httpRequest.getServletRequest().getParameter("type");
            logger.info("param type[{}]",type);
            map.put("type","No "+Constant.Authorization);
            return type.equals("No "+Constant.Authorization);
        }
        String currentUser = getCurrentUser();
        map.put("userName",currentUser);
        //Sec-WebSocket-Protocol
        httpResponse.getServletResponse().setHeader("Sec-WebSocket-Protocol", authorization);
        logger.info("start shaking hands->>>");
        //token 过期校验
        return JWTUtil.verify(authorization,currentUser.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 握手后
     * @param serverHttpRequest request
     * @param serverHttpResponse response
     * @param webSocketHandler handler
     * @param e 异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler,
                               Exception e)
    {
        logger.info("Handshake successful");
    }
}
