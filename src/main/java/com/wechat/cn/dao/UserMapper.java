package com.wechat.cn.dao;

import com.wechat.cn.entry.User;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/26 21:43
 */
public interface UserMapper {

    int insertSelective(User user);
}
