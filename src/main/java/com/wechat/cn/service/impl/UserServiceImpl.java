package com.wechat.cn.service.impl;

import com.wechat.cn.dao.UserMapper;
import com.wechat.cn.entry.User;
import com.wechat.cn.exception.ServiceException;
import com.wechat.cn.service.BaseService;
import com.wechat.cn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/27 16:07
 */
@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Override
    public User getUserInfo() {
        String currentUser = getCurrentUser();
        logger.info("getUserInfo[{}]",currentUser);
        return userMapper.getUserInfoByUserName(currentUser);
    }

    @Override
    @Transactional
    public Boolean updateUserInfo(User user) {
        String currentUser = getCurrentUser();
        User userInfo = userMapper.getUserInfoByUserName(currentUser);
        if (!user.getId().equals(userInfo.getId())){
            throw new ServiceException(1001,"只可以修改自己信息");
        }
        return userMapper.updateByPrimaryKeySelective(user)>0;
    }
}
