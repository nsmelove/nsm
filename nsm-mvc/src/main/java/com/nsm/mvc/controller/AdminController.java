package com.nsm.mvc.controller;

import com.nsm.mvc.bean.Session;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import com.nsm.mvc.service.UserService;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by nieshuming on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/admin")
public class AdminController extends ErrorHandler{
    @Resource
    private AuthService authService;
    @Resource
    private UserService userService;

    public List<User> users(@RequestAttribute String sid, @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        Session session = authService.getSession(sid);
        if(session == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        if(session.getUserType() != User.UserType.ADMIN.ordinal()) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        List<User> users = userService.getUsers(offset, limit);
        return users;
    }
}
