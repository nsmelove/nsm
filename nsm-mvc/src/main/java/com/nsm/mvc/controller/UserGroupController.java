package com.nsm.mvc.controller;

import com.nsm.core.entity.UserGroup;
import com.nsm.core.exception.BusinessException;
import com.nsm.bean.ErrorCode;
import com.nsm.core.service.UserGroupService;
import com.nsm.core.service.UserService;
import com.nsm.core.pojo.GroupInviteInfo;
import com.nsm.core.pojo.GroupMemberInfo;
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
    @Resource
    private UserService userService;

    @RequestMapping("/list")
    @ResponseBody
    public List<UserGroup> groupList(@RequestAttribute long uid, @RequestAttribute String sid, @RequestParam(required = false) Integer admin){
        return userGroupService.getUserGroups(uid, admin);
    }

    @RequestMapping("/create")
    @ResponseBody
    public long createGroup(@RequestAttribute long uid, @RequestAttribute String sid,
                            @RequestParam String name, @RequestParam(required = false) Long parentId){
        parentId = parentId == null ? 0 : parentId;
        return userGroupService.createGroup(uid, name, parentId);
    }

    @RequestMapping("/{gid}/rename")
    public void renameGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid, @RequestParam String name){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.renameGroup(gid, name);
    }

    @RequestMapping("/{gid}/delete")
    public void deleteGroup(@RequestAttribute long uid, @RequestAttribute String sid, @PathVariable long gid){
        if(userGroupService.isGroupMember(uid, gid) != 3){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.deleteGroup(gid);
    }

    @RequestMapping("/{gid}/member/{memberId}/add")
    public void addMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @PathVariable long memberId){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.addGroupMember(uid, gid, memberId);
    }

    @RequestMapping("/{gid}/member/list")
    public List<GroupMemberInfo> groupMemberList(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        if(userGroupService.isGroupMember(uid, gid) == 0){
            UserGroup userGroup = userGroupService.getUserGroup(gid);
            if(userGroup == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
            if(userGroup.getPrivacy() == UserGroup.GroupPrivacy.PRIVATE.ordinal() ||
                    (userGroup.getPrivacy() == UserGroup.GroupPrivacy.CLOSED.ordinal() && false)) {//TODO 创建者联系人可以看见
                throw new BusinessException(ErrorCode.NO_PERMISSION);
            }
        }
        return userGroupService.groupMemberInfoList(gid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/{gid}/member/{memberId}/setAdmin")
    public void setAdmin(@RequestAttribute long uid, @RequestAttribute String sid,
                         @PathVariable long gid, @PathVariable long memberId, @RequestParam boolean admin){
        if(userGroupService.isGroupMember(uid, gid) != 3){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.setGroupAdmin(uid, gid, memberId, admin);
    }

    @RequestMapping("{gid}/member/{memberId}/silence")
    public void silence(@RequestAttribute long uid, @RequestAttribute String sid,
                       @PathVariable long gid, @PathVariable long memberId, @RequestParam boolean silent){
        int isMember = userGroupService.isGroupMember(memberId, gid);
        if(isMember == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        int userIsMember = userGroupService.isGroupMember(uid, gid);
        if(userIsMember <= isMember) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.silenceGroupMember(uid, gid, memberId, silent);
    }

    @RequestMapping("/{gid}/member/{memberId}/remove")
    public void removeMember(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam long memberId){
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        userGroupService.removeGroupMember(uid, gid, memberId);
    }


    @RequestMapping("/{gid}/invite/list")
    public List<GroupInviteInfo> inviteList(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        if(userGroupService.isGroupMember(uid, gid) <= 1){
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        return userGroupService.groupInviteInfoList(gid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/invite/list")
    public List<GroupInviteInfo> inviteList(@RequestAttribute long uid, @RequestAttribute String sid,
                                        @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        return userGroupService.userInviteInfoList(uid, offset, limit > 0 ? limit : 20);
    }

    @RequestMapping("/{gid}/invite/agree")
    public void agreeJoin(@RequestAttribute long uid, @RequestAttribute String sid,
                          @PathVariable long gid){
        userGroupService.agreeJoinGroup(uid, gid);
    }
}
