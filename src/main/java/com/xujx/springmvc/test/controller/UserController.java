package com.xujx.springmvc.test.controller;

import com.xujx.springmvc.annotation.Controller;
import com.xujx.springmvc.annotation.RequestMapping;
import com.xujx.springmvc.annotation.Resource;
import com.xujx.springmvc.test.service.UserService;
import com.xujx.springmvc.test.service.impl.UserServiceImpl;

/**
 * Created by xujinxin on 2017/8/7.
 */
@Controller(clazz = UserController.class)
public class UserController {

    @Resource(clazz = UserServiceImpl.class)
    private UserService userService;


    @RequestMapping(path = "/insert")
    public void insert() {
        userService.insert();
    }
}
