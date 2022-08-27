package com.wechat.cn.service;

import com.wechat.cn.entry.User;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/27 16:06
 */
public interface UserService {
    User getUserInfo();

    Boolean updateUserInfo(User user);
}
