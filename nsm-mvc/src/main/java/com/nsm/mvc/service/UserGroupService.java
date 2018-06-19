package com.nsm.mvc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.GroupInvite;
import com.nsm.mvc.bean.GroupMember;
import com.nsm.mvc.bean.UserGroup;
import com.nsm.mvc.config.SystemConfig;
import com.nsm.mvc.dao.GroupInviteDao;
import com.nsm.mvc.dao.GroupMemberDao;
import com.nsm.mvc.dao.UserGroupDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/18.
 */
@Service
public class UserGroupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserGroupDao userGroupDao;
    @Resource
    GroupMemberDao groupMemberDao;
    @Resource
    GroupInviteDao groupInviteDao;

    /**
     * 获取用户的群组
     * @param userId 用户Id
     * @param adminStatus 管理状态；0.不区分，1.用户创建的，2.用户是管理员（包括创建的），3.不是管理员
     * @return
     */
    public List<UserGroup> getUserGroups(long userId, int adminStatus) {
        List<UserGroup> groups = null;
        if(adminStatus == 1) {
            groups = userGroupDao.getUserGroups(0, userId, 0, Integer.MAX_VALUE);
        }else {
            //groupMemberDao.getGroupMember()
        }
        return groups;
    }

    public long createGroup(long userId, String groupName, long parentGroupId) {
        int userGroupCount = userGroupDao.countUserGroup(0, userId);
        if(userGroupCount >= SystemConfig.userGroupLimit){
            throw new BusinessException(ErrorCode.USER_GROUP_LIMIT);
        }
        UserGroup parentGroup = null;
        if(parentGroupId > 0) {
            parentGroup = userGroupDao.getUserGroup(parentGroupId);
            if(parentGroup == null){
                throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
            }
            if(parentGroup.getCreatorId() != userId){
                throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
            }
            if(parentGroup.getGroupLevel() >= SystemConfig.groupLevelLimit) {
                throw new BusinessException(ErrorCode.USER_GROUP_LEVEL_LIMIT);
            }
        }
        long now = System.currentTimeMillis();
        UserGroup newGroup = new UserGroup();
        newGroup.setCreatorId(userId);
        newGroup.setGroupId(IdUtils.nextLong());
        newGroup.setGroupLevel(parentGroup != null ? (short)(parentGroup.getGroupLevel() + 1) : (short)0);
        newGroup.setGroupName(groupName);
        newGroup.setCreateTime(now);
        newGroup.setParGroupId(parentGroupId);
        userGroupDao.addUserGroup(newGroup);

        if(parentGroup != null) {
            UserGroup.Update updateParam = new UserGroup.Update();
            updateParam.addSubGIds = Lists.newArrayList(newGroup.getGroupId());
            userGroupDao.updateUserGroup(parentGroup.getGroupId(), updateParam);
        }

        GroupMember memCreator = new GroupMember();
        memCreator.setGroupId(newGroup.getGroupId());
        memCreator.setMemberId(userId);
        memCreator.setAdmin(true);
        memCreator.setJoinTime(now);
        groupMemberDao.addGroupMember(memCreator);
        return newGroup.getGroupId();
    }

    public void deleteGroup(long userId, long groupId) {
        UserGroup group = userGroupDao.getUserGroup(groupId);
        if(group == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(group.getCreatorId() != userId) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        if(CollectionUtils.isNotEmpty(group.getSubGroupIds())) {
            Map<Long, UserGroup> supGroupMap = Maps.newHashMap();
            LinkedList<UserGroup> groupQueue = Lists.newLinkedList();
            groupQueue.addAll(userGroupDao.batchGetUserGroup(group.getSubGroupIds()));
            while (!groupQueue.isEmpty()){
                UserGroup subGroup = groupQueue.poll();
                if(!supGroupMap.containsKey(subGroup.getGroupId())){
                    supGroupMap.put(subGroup.getGroupId(), subGroup);
                    groupQueue.addAll(userGroupDao.batchGetUserGroup(subGroup.getSubGroupIds()));
                }
            }
            if(!supGroupMap.isEmpty()){
                userGroupDao.batchDeleteUserGroup(supGroupMap.keySet());
                supGroupMap.forEach((subGroupId, subGroup) ->{
                    groupMemberDao.deleteGroupMembers(subGroupId);
                    groupInviteDao.deleteGroupGroupInvites(subGroupId, 0);
                });
            }
        }

        userGroupDao.deleteUserGroup(groupId);
        groupMemberDao.deleteGroupMembers(groupId);
        groupInviteDao.deleteGroupGroupInvites(groupId, 0);

        if(group.getParGroupId() > 0){
            UserGroup.Update update = new UserGroup.Update();
            update.delSubGIds = Lists.newArrayList(group.getGroupId());
            userGroupDao.updateUserGroup(group.getParGroupId(), update);
        }
    }

    public void addGroupMember(long userId, long groupId, long memberId) {
        GroupMember currentMember = groupMemberDao.getGroupMember(groupId,userId);
        if(currentMember == null && !currentMember.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        //TODO refer to {@link UserSetting}
        GroupMember newMember = new GroupMember();
        newMember.setGroupId(groupId);
        newMember.setMemberId(memberId);
        newMember.setJoinTime(System.currentTimeMillis());
        groupMemberDao.addGroupMember(newMember);
    }

    public void agreeJoinGroup(long userId, long groupId) {
        GroupInvite invite = groupInviteDao.getGroupInvite(groupId, userId);
        if(invite == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        groupInviteDao.deleteGroupInvite(groupId, userId);
        GroupMember newMember = new GroupMember();
        newMember.setGroupId(groupId);
        newMember.setMemberId(userId);
        newMember.setJoinTime(System.currentTimeMillis());
        groupMemberDao.addGroupMember(newMember);
    }

    public void setGroupAdmin(long userId, long groupId, long memberId, boolean admin) {
        UserGroup group = userGroupDao.getUserGroup(groupId);
        if(group == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(group.getCreatorId() != userId) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        GroupMember member = groupMemberDao.getGroupMember(groupId, memberId);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(member.isAdmin()) {
            return;
        }
        groupMemberDao.updateGroupMember(groupId, memberId, true, null);
    }

    public void silentGroupMember(long userId, long groupId, long memberId, boolean silent) {
        UserGroup group = userGroupDao.getUserGroup(groupId);
        if(group == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        GroupMember currentMember = groupMemberDao.getGroupMember(groupId,userId);
        if(currentMember == null){
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        GroupMember member = groupMemberDao.getGroupMember(groupId, memberId);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(!currentMember.isAdmin() || (member.isAdmin() && userId != group.getCreatorId())) {
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        groupMemberDao.updateGroupMember(groupId, memberId, null, silent);
    }

    public void removeGroupMember(long userId, long groupId, long memberId) {
        GroupMember currentMember = groupMemberDao.getGroupMember(groupId,userId);
        if(currentMember == null && !currentMember.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTHENTICATION);
        }
        GroupMember member = groupMemberDao.deleteGroupMember(groupId, memberId);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
    }

}
