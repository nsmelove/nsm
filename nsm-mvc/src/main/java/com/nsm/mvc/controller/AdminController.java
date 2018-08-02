package com.nsm.mvc.controller;

import com.nsm.core.entity.ProductCategory;
import com.nsm.core.entity.ProductProperty;
import com.nsm.core.entity.User;
import com.nsm.core.exception.BusinessException;
import com.nsm.bean.ErrorCode;
import com.nsm.core.service.AdminService;
import com.nsm.core.service.SessionService;
import com.nsm.core.service.UserService;
import com.nsm.core.pojo.UserInfo;
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
    private AdminService adminService;
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
                            @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer userType, @RequestParam(required = false) Integer status){
        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        return adminService.userList(uid, offset, limit, userType, status);
    }

    /**
     * 禁用用户
     * @param uid
     * @param userId
     * @param forbid
     */
    @RequestMapping("/user/{userId}/forbid")
    public void forbid(@RequestAttribute long uid, @PathVariable long userId, @RequestParam boolean forbid){
        adminService.forbid(uid, userId, forbid);
    }

    /**
     * 设置用户系统管理员
     * @param uid
     * @param userId
     * @param admin
     */
    @RequestMapping("/user/{userId}/setAdmin")
    public void setAdmin(@RequestAttribute long uid, @PathVariable long userId, @RequestParam boolean admin){
        adminService.setAdmin(uid, userId, admin);
    }

    @RequestMapping("/group/list")
    @ResponseBody
    public List<Object> groupList(@RequestAttribute long uid,
                            @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer userType, @RequestParam(required = false) Integer status){
        //TODO
        //return adminService.groupList(uid, offset, limit, userType, status);
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
                                                  @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
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
                                   @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        //TODO
        return null;
    }
}
