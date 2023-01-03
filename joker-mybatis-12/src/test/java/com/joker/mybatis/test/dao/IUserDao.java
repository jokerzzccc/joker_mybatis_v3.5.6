package com.joker.mybatis.test.dao;

import com.joker.mybatis.test.po.User;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jokerzzccc
 * @date 2022/10/16
 */
public interface IUserDao {

    User queryUserInfoById(long uId);

    User queryUserInfo(User req);

    List<User> queryUserInfoList();

    int updateUserInfo(User req);

    void insertUserInfo(User req);

    int deleteUserInfoByUserId(String userId);

}