package com.nsm.mvc.controller;

import com.google.common.collect.Lists;
import com.nsm.mvc.bean.GroupInvite;
import com.nsm.mvc.bean.GroupMember;
import com.nsm.mvc.bean.UserGroup;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.service.UserGroupService;
import com.nsm.mvc.service.UserService;
import com.nsm.mvc.view.GroupInviteInfo;
import com.nsm.mvc.view.GroupMemberInfo;
import com.nsm.mvc.view.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Resource
    private UserService userService;

    @RequestMapping("/list")
    @ResponseBody
    public List<UserGroup> groupList(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam(required = false) int adminStatus){
        return userGroupService.getUserGroups(uid, adminStatus);
    }

    @RequestMapping("/create")
    @ResponseBody
    public long createGroup(@RequestAttribute long uid, @RequestAttribute String sid,
                            @RequestParam String groupName, @RequestParam(required = false) long parentGroupId){
        return userGroupService.createGroup(uid, groupName, parentGroupId);
    }

    @RequestMapping("/{gid}/rename")
    public void renameGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid, @RequestParam String groupName){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.renameGroup(gid, groupName);
    }

    @RequestMapping("/{gid}/delete")
    public void deleteGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid){
        if(userGroupService.isGroupMember(uid, gid) != 3){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.deleteGroup(gid);
    }

    @RequestMapping("/{gid}/member/add")
    public void addMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long memberId){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.addGroupMember(uid, gid, memberId);
    }

    @RequestMapping("/{gid}/member/list")
    public List<GroupMemberInfo> groupMemberList(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        if(userGroupService.isGroupMember(uid, gid) == 0){
            UserGroup userGroup = userGroupService.getUserGroup(gid);
            if(userGroup == null) {
                throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
            }
            if(userGroup.getPrivacy() == UserGroup.GroupPrivacy.PRIVATE.ordinal() ||
                    (userGroup.getPrivacy() == UserGroup.GroupPrivacy.CLOSED.ordinal() && false)) {//TODO 创建者联系人可以看见
                throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
            }
        }
        return userGroupService.groupMemberInfoList(gid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/{gid}/member/{mid}/setAdmin")
    public void setAdmin(@RequestAttribute long uid, @RequestAttribute String sid,
                         @PathVariable long gid, @PathVariable long mid, @RequestParam boolean admin){
        if(userGroupService.isGroupMember(uid, gid) != 3){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.setGroupAdmin(uid, gid, mid, admin);
    }

    @RequestMapping("{gid}/member/{mid}/silence")
    public void silence(@RequestAttribute long uid, @RequestAttribute String sid,
                       @PathVariable long gid, @PathVariable long mid, @RequestParam boolean silent){
        int isMember = userGroupService.isGroupMember(mid, gid);
        if(isMember == 0) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        int userIsMember = userGroupService.isGroupMember(uid, gid);
        if(userIsMember <= isMember) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.silenceGroupMember(uid, gid, mid, silent);
    }

    @RequestMapping("/{gid}/member/{mid}/remove")
    public void removeMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long mid){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        userGroupService.removeGroupMember(uid, gid, mid);
    }


    @RequestMapping("/{gid}/invite/list")
    public List<GroupInviteInfo> inviteList(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        return userGroupService.groupInviteInfoList(gid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/invite/list")
    public List<GroupInviteInfo> inviteList(@RequestAttribute long uid, @RequestAttribute String sid,
                                        @RequestParam(required = false) int offset, @RequestParam(required = false) int limit){
        return userGroupService.userInviteInfoList(uid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/{gid}/invite/agree")
    public void agreeJoin(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid){
        userGroupService.agreeJoinGroup(uid, gid);
    }
}
