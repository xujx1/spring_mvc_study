package com.xujx.springmvc.test.dao.impl;

import com.xujx.springmvc.annotation.Service;
import com.xujx.springmvc.test.dao.UserDao;

/**
 * Created by xujinxin on 2017/8/7.
 */

@Service(clazz = UserDaoImpl.class)
public class UserDaoImpl implements UserDao {
    @Override
    public void insert() {
        System.out.println("insert");
    }
}
