package com.nsm.mvc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.GroupInvite;
import com.nsm.mvc.bean.GroupMember;
import com.nsm.mvc.bean.UserGroup;
import com.nsm.mvc.bean.UserSetting;
import com.nsm.mvc.config.SystemConfig;
import com.nsm.mvc.dao.GroupInviteDao;
import com.nsm.mvc.dao.GroupMemberDao;
import com.nsm.mvc.dao.UserGroupDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.view.GroupInviteInfo;
import com.nsm.mvc.view.GroupMemberInfo;
import com.nsm.mvc.view.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 小组成员服务类
 *
 * @author Created by nsm on 2018/6/18.
 */
@Service
public class UserGroupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserGroupDao userGroupDao;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Resource
    private GroupInviteDao groupInviteDao;
    @Resource
    private UserService userService;

    /**
     * 用户是不是小组成员
     *
     * @param userId 用户Id
     * @param groupId 小组Id
     * @return 0.不是，1.是，2.是小组管理员，3.小组创建者
     */
    public int isGroupMember(long userId, long groupId){
        GroupMember groupMember= groupMemberDao.getGroupMember(groupId,userId);
        if(groupMember == null){
            return 0;
        }else if(groupMember.isAdmin()) {
            UserGroup userGroup = userGroupDao.getUserGroup(groupId);
            if(userGroup != null && userGroup.getCreatorId() == userId) {
                return 3;
            }else {
                return 2;
            }
        }else {
            return 1;
        }
    }

    /**
     * 获取一个群组
     *
     * @param groupId 群组Id
     * @return 群组
     */
    public UserGroup getUserGroup(long groupId){
        return userGroupDao.getUserGroup(groupId);
    }

    /**
     * 批量获取用户群组
     *
     * @param groupIds 群组Id集合
     * @return 群组集合映射
     */
    public Map<Long, UserGroup> batchGetUserGroup(Collection<Long> groupIds){
        return userGroupDao.batchGetUserGroup(groupIds).stream().collect(Collectors.toMap(UserGroup::getGroupId, Function.<UserGroup>identity()));
    }
    /**
     * 获取用户的群组
     *
     * @param userId 用户Id
     * @param adminStatus 管理状态；-1.不区分，0.不是管理员，1.用户是管理员（包括创建的），2.用户是创建者
     * @return 用户的群组列表
     */
    public List<UserGroup> getUserGroups(long userId, int adminStatus) {
        List<UserGroup> groups;
        if(adminStatus == 2) {
            groups = userGroupDao.getUserGroups(0, userId, 0, Integer.MAX_VALUE);
        }else {
            List<Long> groupIds = groupMemberDao.getMemberGroupIds(userId, adminStatus == -1 ? null : (adminStatus == 1));
            groups = userGroupDao.batchGetUserGroup(groupIds);
        }
        return groups;
    }

    /**
     * 创建用户群组
     *
     * @param userId 创建者Id
     * @param groupName 群组名称
     * @param parentGroupId 父级群组Id
     * @return 用户群组Id
     */
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

    /**
     * 重命名群组
     *
     * @param groupId 群组Id
     * @param groupName 新群组名称
     */
    public void renameGroup(long groupId, String groupName) {
        UserGroup.Update updateParam = new UserGroup.Update();
        updateParam.groupName = groupName;
        userGroupDao.updateUserGroup(groupId, updateParam);
    }

    /**
     * 删除小组
     * @param groupId 小组Id
     */
    public void deleteGroup(long groupId) {
        UserGroup group = userGroupDao.getUserGroup(groupId);
        if(group == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(CollectionUtils.isNotEmpty(group.getSubGroupIds())) {
            Set<Long> supGroupSet = Sets.newHashSet();
            LinkedList<UserGroup> groupQueue = Lists.newLinkedList();
            groupQueue.addAll(userGroupDao.batchGetUserGroup(group.getSubGroupIds()));
            while (!groupQueue.isEmpty()){
                UserGroup subGroup = groupQueue.poll();
                if(!supGroupSet.contains(subGroup.getGroupId())){
                    supGroupSet.add(subGroup.getGroupId());
                    groupQueue.addAll(userGroupDao.batchGetUserGroup(subGroup.getSubGroupIds()));
                }
            }
            if(!supGroupSet.isEmpty()){
                userGroupDao.batchDeleteUserGroup(supGroupSet);
                supGroupSet.forEach(subGroupId ->{
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

    /**
     * 添加小组成员
     *
     * @param userId 添加者Id
     * @param groupId 小组Id
     * @param memberId 成员Id
     */
    public void addGroupMember(long userId, long groupId, long memberId) {
        UserSetting memberUserSetting = userService.getUserSetting(memberId);
        if(memberUserSetting == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        UserGroup userGroup = userGroupDao.getUserGroup(groupId);
        if(userGroup == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(userGroup.getParGroupId() > 0) {
            if(groupMemberDao.getGroupMember(userGroup.getParGroupId(), memberId) == null){
                throw new BusinessException(ErrorCode.GROUP_MEMBER_NOT_IN_PARENT);
            }
        }
        int memberCount = groupMemberDao.countGroupMember(groupId, 0);
        if(memberCount >= SystemConfig.groupMemberLimit) {
            throw new BusinessException(ErrorCode.GROUP_MEMBER_LIMIT);
        }
        long now = System.currentTimeMillis();
        if(memberUserSetting.isAutoJoinGroup()) {
            GroupMember newMember = new GroupMember();
            newMember.setGroupId(groupId);
            newMember.setMemberId(memberId);
            newMember.setJoinTime(now);
            groupMemberDao.addGroupMember(newMember);
        }else {
            GroupInvite invite = new GroupInvite();
            invite.setGroupId(groupId);
            invite.setInviterId(userId);
            invite.setInviteeId(memberId);
            invite.setInviteTime(now);
            groupInviteDao.addGroupInvite(invite);
        }
    }

    /**
     * 小组成员列表
     *
     * @param groupId 小组Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 成员列表
     */
    public List<GroupMember> groupMemberList(long groupId, int offset, int limit) {
        return groupMemberDao.getGroupMembers(groupId, offset, limit);
    }

    /**
     * 小组成员信息列表
     *
     * @param groupId 小组Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 成员列表
     */
    public List<GroupMemberInfo> groupMemberInfoList(long groupId, int offset, int limit) {
        List<GroupMember> members = groupMemberList(groupId, offset, limit);
        Map<Long, UserInfo> userInfoMap = userService.batchGetUserInfo(members.stream().map(GroupMember::getMemberId).collect(Collectors.toList()));
        return Lists.transform(members, member -> {
            GroupMemberInfo memberInfo = new GroupMemberInfo();
            memberInfo.setGroupId(member.getGroupId());
            memberInfo.setMemberId(member.getMemberId());
            memberInfo.setAdmin(member.isAdmin());
            memberInfo.setSilent(member.isSilent());
            memberInfo.setJoinTime(member.getJoinTime());
            UserInfo userInfo = userInfoMap.get(member.getMemberId());
            if (userInfo != null) {
                memberInfo.setNickname(userInfo.getNickname());
                memberInfo.setUserIcon(userInfo.getUserIcon());
            }
            return memberInfo;
        });
    }


    /**
     * 设置小组管理员
     *
     * @param userId 操作用户Id
     * @param groupId 小组Id
     * @param memberId 成员Id
     * @param admin 设置或取消管理员
     */
    public void setGroupAdmin(long userId, long groupId, long memberId, boolean admin) {
        GroupMember member = groupMemberDao.updateGroupMember(groupId, memberId, admin, null);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(member.isAdmin() != admin) {
            //TODO 发通知
        }
    }

    /**
     * 禁言小组成员
     *
     * @param userId 操作者Id
     * @param groupId 小组Id
     * @param memberId 成员Id
     * @param silent 是否禁言
     */
    public void silenceGroupMember(long userId, long groupId, long memberId, boolean silent) {
        GroupMember member = groupMemberDao.updateGroupMember(groupId, memberId, null, silent);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(member.isSilent() != silent) {
            //TODO 发通知
        }
    }

    /**
     * 移除小组成员
     *
     * @param userId 操作者Id
     * @param groupId 小组Id
     * @param memberId 成员Id
     */
    public void removeGroupMember(long userId, long groupId, long memberId) {
        UserGroup userGroup = userGroupDao.getUserGroup(groupId);
        if(userGroup == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }

        GroupMember member = groupMemberDao.deleteGroupMember(groupId, memberId);
        if(member == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        //TODO 发通知
        if(CollectionUtils.isNotEmpty(userGroup.getSubGroupIds())){
            LinkedList<UserGroup> groupQueue = Lists.newLinkedList();
            groupQueue.addAll(userGroupDao.batchGetUserGroup(userGroup.getSubGroupIds()));
            while (!groupQueue.isEmpty()){
                UserGroup subGroup = groupQueue.poll();
                member = groupMemberDao.deleteGroupMember(subGroup.getGroupId(), memberId);
                if(member != null){
                    groupQueue.addAll(userGroupDao.batchGetUserGroup(subGroup.getSubGroupIds()));
                    //TODO 发通知
                }
            }
        }
    }

    private List<GroupInviteInfo> inviteToInfos(List<GroupInvite> invites){
        if (CollectionUtils.isEmpty(invites)) {
            return Collections.emptyList();
        }
        Set<Long> groupIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        for(GroupInvite invite : invites) {
            groupIdSet.add(invite.getGroupId());
            userIdSet.add(invite.getInviterId());
            userIdSet.add(invite.getInviteeId());
        }
        Map<Long, UserGroup> userGroupMap = batchGetUserGroup(groupIdSet);
        Map<Long, UserInfo> userInfoMap = userService.batchGetUserInfo(userIdSet);
        List<GroupInviteInfo> inviteInfos = Lists.newArrayListWithCapacity(invites.size());
        for(GroupInvite invite : invites) {
            GroupInviteInfo inviteInfo = new GroupInviteInfo();
            inviteInfo.setGroupId(invite.getGroupId());
            inviteInfo.setInviterId(invite.getInviterId());
            inviteInfo.setInviteeId(invite.getInviteeId());
            inviteInfo.setInviteTime(invite.getInviteTime());
            UserGroup userGroup = userGroupMap.get(invite.getGroupId());
            if(userGroup != null) {
                inviteInfo.setGroupName(userGroup.getGroupName());
            }
            UserInfo inviterInfo = userInfoMap.get(invite.getInviterId());
            if(inviterInfo != null) {
                inviteInfo.setInviterName(inviterInfo.getNickname());
                inviteInfo.setInviterIcon(inviterInfo.getUserIcon());
            }
            UserInfo inviteeInfo = userInfoMap.get(invite.getInviteeId());
            if(inviteeInfo != null) {
                inviteInfo.setInviterName(inviteeInfo.getNickname());
                inviteInfo.setInviterIcon(inviteeInfo.getUserIcon());
            }
            inviteInfos.add(inviteInfo);
        }
        return inviteInfos;
    }

    /**
     * 小组要请列表
     *
     * @param groupId 小组Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 邀请列表
     */
    public List<GroupInvite> groupInviteList(long groupId, int offset, int limit) {
        return groupInviteDao.getGroupInvites(groupId, 0, offset, limit);
    }

    /**
     * 小组要请信息列表
     *
     * @param groupId 小组Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 邀请信息列表
     */
    public List<GroupInviteInfo> groupInviteInfoList(long groupId, int offset, int limit) {
        List<GroupInvite> groupInvites = groupInviteList(groupId, offset, limit);
        return inviteToInfos(groupInvites);
    }

    /**
     * 用户收到的邀请列表
     *
     * @param userId 用户Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 邀请列表
     */
    public List<GroupInvite> userInviteList(long userId, int offset, int limit) {
        return groupInviteDao.getGroupInvites(0, userId, offset, limit);
    }

    /**
     * 用户收到的邀请详细信息列表
     *
     * @param userId 用户Id
     * @param offset 偏移量
     * @param limit 记录数
     * @return 详细信息列表
     */
    public List<GroupInviteInfo> userInviteInfoList(long userId, int offset, int limit) {
        List<GroupInvite> groupInvites = userInviteList(userId, offset, limit);
        return inviteToInfos(groupInvites);
    }

    /**
     * 用户同意加入小组
     *
     * @param userId 用户Id
     * @param groupId 小组Id
     */
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
}
