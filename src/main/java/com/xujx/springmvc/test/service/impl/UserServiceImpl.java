package com.xujx.springmvc.test.service.impl;

import com.xujx.springmvc.annotation.Resource;
import com.xujx.springmvc.annotation.Service;
import com.xujx.springmvc.test.dao.UserDao;
import com.xujx.springmvc.test.dao.impl.UserDaoImpl;
import com.xujx.springmvc.test.service.UserService;

/**
 * Created by xujinxin on 2017/8/7.
 */
@Service(clazz = UserServiceImpl.class)
public class UserServiceImpl implements UserService {

    @Resource(clazz = UserDaoImpl.class)
    private UserDao userDao;

    @Override
    public void insert() {
        userDao.insert();
    }
}
