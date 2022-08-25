package com.wechat.cn.dao;

import com.wechat.cn.entry.WechatUser;

/**
* Created by IntelliJ IDEA.
* @Author : 镜像
* @create 2022/8/25 21:11
*/
public interface WechatUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WechatUser record);

    int insertSelective(WechatUser record);

    WechatUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WechatUser record);

    int updateByPrimaryKey(WechatUser record);
}