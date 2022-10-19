package com.joker.mybatis.test.dao;

import com.joker.mybatis.test.po.User;

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

}