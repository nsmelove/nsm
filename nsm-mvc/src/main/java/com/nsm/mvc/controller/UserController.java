package com.nsm.mvc.controller;

import com.nsm.mvc.bean.User;
import com.nsm.mvc.bean.UserSetting;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import com.nsm.mvc.service.UserService;
import com.nsm.mvc.view.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/user")
public class UserController extends ErrorHandler{
    @Resource
    private AuthService authService;
    @Resource
    private UserService userService;

    /***
     * 注册用户
     * @param username 用户名
     * @param password 密码
     * @return 用户Id
     */
    @RequestMapping(value = "/register")
    @ResponseBody
    public long register(@RequestParam String username, @RequestParam String nickname, @RequestParam String password){
        return userService.register(username, nickname, password);
    }

    /**
     * 用户登陆
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @RequestMapping("/login")
    @ResponseBody
    public UserInfo login(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam String username, @RequestParam String password){
        return userService.login(uid, sid, username, password);
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
    public UserInfo current(@RequestAttribute long uid){
        UserInfo userInfo = userService.getUserInfo(uid);
        if(userInfo == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        return userInfo;
    }

    @RequestMapping("/changeInfo")
     public void changeInfo(@RequestAttribute long uid, ServletRequest request){
        User.Update update = new User.Update(uid);
        String nickname = request.getParameter("nickname");
        if(nickname != null) {
            update.nickname = nickname;
        }
        String userIcon = request.getParameter("userIcon");
        if(userIcon != null) {
            update.userIcon = userIcon;
        }
        String privacy = request.getParameter("privacy");
        if(privacy != null) {
            update.privacy = Integer.valueOf(privacy);
        }
        userService.changeInfo(update);
    }


    @RequestMapping("/changePwd")
    public void changePwd(@RequestAttribute long uid, @RequestParam String oldPwd, @RequestParam String newPwd){
        userService.changePwd(uid, oldPwd, newPwd);
    }

    @RequestMapping("/setting")
    @ResponseBody
    public UserSetting getSetting(@RequestAttribute long uid){
        return userService.getUserSetting(uid);
    }

    @RequestMapping("/changeSetting")
    public void changeSetting(@RequestAttribute long uid, ServletRequest request){
        UserSetting.Update update = new UserSetting.Update(uid);
        String autoJoinGroup = request.getParameter("autoJoinGroup");
        if(autoJoinGroup != null) {
            update.autoJoinGroup = Boolean.valueOf(autoJoinGroup);
        }
        userService.changeSetting(update);
    }

    /**
     * 获取用户信息
     * @param uid 用户Id
     * @return 用户信息
     */
    @RequestMapping("/{uid}")
    @ResponseBody
    public UserInfo user(@PathVariable long uid){
        UserInfo userInfo = userService.getUserInfo(uid);
        if(userInfo == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        return userInfo;
    }

}
