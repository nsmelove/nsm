package com.nsm.core;

import com.nsm.core.dao.UserDao;
import com.nsm.core.service.UserService;

/**
 * Created by nieshuming on 2018/6/22
 */
public class SpringContainerTest {

    public static void main(String[] args) {
        UserDao userDao = SpringContainer.getBean(UserDao.class);
        System.out.println(userDao.getUserByUsername("nsm"));
    }
}
