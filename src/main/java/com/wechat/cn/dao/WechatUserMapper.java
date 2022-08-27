package com.wechat.cn.dao;

import com.wechat.cn.entry.WechatUser;
import org.apache.ibatis.annotations.Param;

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

    boolean hasLiveByOpenId(@Param("openId")String openId);

    boolean hasUserInfoByOpenId(@Param("openId")String openId);

    void updateUserInfoByOpenId(@Param("openId") String openId,
                                @Param("unionId") String unionId,
                                @Param("userInfo") String weChatUserInfo);
}