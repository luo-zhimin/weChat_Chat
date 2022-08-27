package com.wechat.cn.dao;

import com.wechat.cn.entry.User;
import org.apache.ibatis.annotations.Param;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/26 21:43
 */
public interface UserMapper {

    User getUserInfoByUserName(@Param("userName")String userName);

    int insertSelective(User user);

    int updateByPrimaryKeySelective(User user);
}
