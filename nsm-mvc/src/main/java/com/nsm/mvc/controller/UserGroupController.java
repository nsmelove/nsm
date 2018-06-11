package com.nsm.mvc.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/group")
public class UserGroupController extends ErrorHandler{

    @RequestMapping("/list")
    @ResponseBody
    public List<Object> groupList(@RequestAttribute long uid, @RequestAttribute String sid){
        //TODO
        return null;
    }

    @RequestMapping("/create")
    @ResponseBody
    public long createGroup(@RequestAttribute long uid, @RequestAttribute String sid,
                            @RequestParam String groupName, @RequestParam(required = false) long parentGroupId){
        //TODO
        return 1;
    }

    @RequestMapping("/{gid}/delete")
    public void deleteGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid){
        //TODO
    }

    @RequestMapping("/{gid}/member/add")
    public void addMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long memberId){
        //TODO
    }

    @RequestMapping("/{gid}/member/{mid}/agree")
    public void agreeJoin(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @PathVariable long mid){
        //TODO
    }

    @RequestMapping("/{gid}/member/{mid}/setAdmin")
    public void setAdmin(@RequestAttribute long uid, @RequestAttribute String sid,
                         @PathVariable long gid, @PathVariable long mid, @RequestParam boolean admin){
        //TODO
    }

    @RequestMapping("{gid}/member/{mid}/silent")
    public void silent(@RequestAttribute long uid, @RequestAttribute String sid,
                       @PathVariable long gid, @PathVariable long mid, @RequestParam boolean silent){
        //TODO
    }

    @RequestMapping("/{gid}/member/{mid}/remove")
    public void removeMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long mid){
        //TODO
    }
}
