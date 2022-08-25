package com.wechat.cn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wechat.cn.dao")
public class WeChatChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeChatChatApplication.class, args);
    }

}
