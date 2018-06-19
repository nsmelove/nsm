package com.nsm.mvc.controller;

import com.nsm.mvc.bean.UserSetting;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import com.nsm.mvc.service.UserService;
import com.nsm.mvc.view.UserInfo;
import org.apache.commons.lang3.StringUtils;
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
    public UserInfo login(@RequestParam String username, @RequestParam String password, @RequestAttribute String sid){
        return userService.login(username, password, sid);
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
        if(uid == 0) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        UserInfo userInfo = userService.getUserInfo(uid);
        if(userInfo == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        return userInfo;
    }

    @RequestMapping("/rename")
     public void rename(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam String name){
        //TODO
    }

    @RequestMapping("/changeIcon")
    public void changeIcon(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam String icon){
        //TODO
    }

    @RequestMapping("/changePwd")
    public void changePwd(@RequestAttribute long uid, @RequestAttribute String sid,
                          @RequestParam String oldPwd, @RequestParam String newPwd){
        //TODO
    }

    @RequestMapping("/changePrivacy")
    public void changePrivacy(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam int privacy){
        //TODO
    }

    @RequestMapping("/changeSetting")
    public void changeSetting(@RequestAttribute long uid, @RequestAttribute String sid, ServletRequest request){
        UserSetting.Update update = new UserSetting.Update();
        update.userId = uid;
        String autoJoinGroup = request.getParameter("autoJoinGroup");
        if(StringUtils.isNotBlank(autoJoinGroup)) {
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
