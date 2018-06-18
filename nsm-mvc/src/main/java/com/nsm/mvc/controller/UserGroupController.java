package com.nsm.mvc.controller;

import com.nsm.mvc.bean.UserGroup;
import com.nsm.mvc.service.UserGroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/group")
public class UserGroupController extends ErrorHandler{

    @Resource
    private UserGroupService userGroupService;

    @RequestMapping("/list")
    @ResponseBody
    public List<UserGroup> groupList(@RequestAttribute long uid, @RequestAttribute String sid){
        return userGroupService.getUserGroups(uid);
    }

    @RequestMapping("/create")
    @ResponseBody
    public long createGroup(@RequestAttribute long uid, @RequestAttribute String sid,
                            @RequestParam String groupName, @RequestParam(required = false) long parentGroupId){
        return userGroupService.createGroup(uid, groupName, parentGroupId);
    }

    @RequestMapping("/{gid}/delete")
    public void deleteGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid){
        userGroupService.deleteGroup(uid, gid);
    }

    @RequestMapping("/{gid}/member/add")
    public void addMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long memberId){
        userGroupService.addGroupMember(uid, gid, memberId);
    }

    @RequestMapping("/{gid}/member/agree")
    public void agreeJoin(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @PathVariable long mid){
        userGroupService.agreeJoinGroup(uid, gid);
    }

    @RequestMapping("/{gid}/member/{mid}/setAdmin")
    public void setAdmin(@RequestAttribute long uid, @RequestAttribute String sid,
                         @PathVariable long gid, @PathVariable long mid, @RequestParam boolean admin){
        userGroupService.setGroupAdmin(uid, gid, mid, admin);
    }

    @RequestMapping("{gid}/member/{mid}/silent")
    public void silent(@RequestAttribute long uid, @RequestAttribute String sid,
                       @PathVariable long gid, @PathVariable long mid, @RequestParam boolean silent){
        userGroupService.silentGroupMember(uid, gid, mid, silent);
    }

    @RequestMapping("/{gid}/member/{mid}/remove")
    public void removeMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long mid){
        userGroupService.removeGroupMember(uid, gid, mid);
    }
}
