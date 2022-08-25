package com.wechat.cn;

import com.wechat.cn.dao.ConfigMapper;
import com.wechat.cn.entry.Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class WeChatChatApplicationTests {

    @Resource
    private ConfigMapper configMapper;

    @Test
    void contextLoads() {
        Config config = configMapper.selectByPrimaryKey(1l);
        System.out.println(config);
    }

}
