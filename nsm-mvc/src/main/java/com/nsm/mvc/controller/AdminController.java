package com.nsm.mvc.controller;

import com.nsm.mvc.bean.ProductCategory;
import com.nsm.mvc.bean.ProductProperty;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.AuthService;
import com.nsm.mvc.service.UserService;
import com.nsm.mvc.view.UserInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/admin")
public class AdminController extends ErrorHandler{
    @Resource
    private AuthService authService;
    @Resource
    private UserService userService;

    /**
     * 用户列表
     * @param uid
     * @param offset
     * @param limit
     * @param userType
     * @param status
     * @return
     */
    @RequestMapping("/user/list")
    @ResponseBody
    public List<User> userList(@RequestAttribute long uid,
                            @RequestParam(required = false) int offset, @RequestParam(required = false) int limit,
                            @RequestParam(required = false) int userType, @RequestParam(required = false) int status){
        UserInfo loginUserInfo = userService.getUserInfo(uid);
        if(loginUserInfo.getUserType() != User.UserType.ADMIN.ordinal()) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        List<User> users = userService.getUsers(offset, limit);
        return users;
    }

    /**
     * 用户禁止使用
     * @param sid
     * @param uid
     * @param forbid
     */
    @RequestMapping("/user/{uid}/forbid")
    public void forbid(@RequestAttribute String sid, @PathVariable long uid, @RequestParam boolean forbid){
        //TODO
    }

    /**
     * 设置用户系统管理员
     * @param sid
     * @param uid
     * @param admin
     */
    @RequestMapping("/user/{uid}/setAdmin")
    public void setAdmin(@RequestAttribute String sid, @PathVariable long uid, @RequestParam boolean admin){
        //TODO
    }

    @RequestMapping("/group/list")
    @ResponseBody
    public List<Object> groupList(@RequestAttribute String sid,
                            @RequestParam(required = false) int offset, @RequestParam(required = false) int limit,
                            @RequestParam(required = false) int userType, @RequestParam(required = false) int status){
        //TODO
        return null;
    }

    /**
     * 禁言小组
     * @param sid
     * @param gid
     * @param forbid
     */
    @RequestMapping("/group/{gid}/silent")
    public void silent(@RequestAttribute String sid, @PathVariable long gid, @RequestParam boolean forbid){
        //TODO
    }

    /**
     * 解散小组
     * @param sid
     * @param gid
     */
    @RequestMapping("/group/{uid}/dissolution")
    public void dissolution(@RequestAttribute String sid, @PathVariable long gid){
        //TODO
    }

    /**
     * 系统默认产品分类
     * @param sid
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping("/product/category/list")
    @ResponseBody
    public List<ProductCategory> productCategorys(@RequestAttribute String sid,
                                                  @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        //TODO
        return null;
    }

    /**
     * 系统默认产品属性
     * @param sid
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping("/product/property/list")
    @ResponseBody
    public List<ProductProperty> productProperties(@RequestAttribute String sid,
                                   @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        //TODO
        return null;
    }
}
