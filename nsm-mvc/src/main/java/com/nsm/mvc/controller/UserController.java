package com.nsm.mvc.controller;

import com.nsm.commom.utils.IdUtils;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.dao.UserDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2018/5/27.
 */
@RestControllerAdvice
@RequestMapping("/user")
public class UserController extends ErrorHandler{
    @Resource
    private UserDao userDao;
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<User> users(@RequestParam(value = "offset", required = false)Integer offset, @RequestParam(value = "offset",required = false)Integer limit){
        if(offset == null) {
            offset = 0;
        }
        if(limit == null) {
            limit = 10;
        }
        List<User> users =userDao.getUsers(offset, limit);
        return users;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public long users(@RequestBody User user){
        long userId = IdUtils.nextLong();
        user.setUserId(userId);
        userDao.addUser(user);
        return  userId;
    }

    @RequestMapping("/{id}")
    @ResponseBody
    public User user(@PathVariable long id){
        User user =userDao.getUser(id);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return user;
    }
}
