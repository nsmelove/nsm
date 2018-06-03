package com.nsm.mvc.controller;

import com.google.common.hash.Hashing;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.dao.UserDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2018/5/27.
 */
@RestControllerAdvice
@RequestMapping("/user")
public class UserController extends ErrorHandler{
   @Resource
    private UserDao userDao;
    @Resource
    private AuthService authService;

    /***
     * 注册用户
     * @param username 用户名
     * @param password 密码
     * @return 用户Id
     */
    @RequestMapping(value = "/register")
    @ResponseBody
    public long register(@RequestParam String username, @RequestParam String password){

        long userId = IdUtils.nextLong();
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        userDao.addUser(user);
        return userId;
    }

    /**
     * 用户登陆
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @RequestMapping("/login")
    @ResponseBody
    public User login(@RequestParam String username, @RequestParam String password, @RequestAttribute String sid){
        User user = userDao.getUserByUsername(username);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_WRONG);
        }
        String hashKey = user.getUserId() + password;
        if(Hashing.md5().hashString(hashKey).toString().equals(user.getPassword())){
            Session session =authService.newSession(sid, user.getUserId());
            if(session == null) {
                throw new BusinessException(new ErrorCode(500,"登陆失败"));
            }
            return user;
        }else {
            throw new BusinessException(ErrorCode.USER_PASSWORD_WRONG);
        }
    }

    /**
     * 用户登出
     * @param uid 用户Id
     * @param sid 会话id
     */
    @RequestMapping("/logout")
    public void logout(@RequestAttribute long uid, @RequestAttribute String sid){
        authService.remSession(sid);

    }

    /**
     * 获取当前用户信息
     * @param uid 用户Id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public User current(@RequestAttribute long uid){
        User user = userDao.getUser(uid);
        if(user == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.UNAUTHORIZED));
        }
        return user;
    }

    /**
     * 获取用户信息
     * @param id 用户Id
     * @return 用户信息
     */
    @RequestMapping("/{id}")
    @ResponseBody
    public User user(@PathVariable long id){
        User user = userDao.getUser(id);
        if(user == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        return user;
    }

}
