package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * 用户小组
 *
 * Created by nsm on 2018/6/11
 */
public class UserGroup {

    @Id
    private long groupId;

    private long creatorId;

    private String groupName;

    private int privacy;
    /**
     * 小组是否被禁言
     */
    boolean silent;

    private long parGroupId;

    private List<Long> subGroupIds;

    private List<GroupMember> members;

    private long createTime;

    public enum GroupPrivacy{
        OPEN,//公开小组
        CLOSED,//封闭小组
        PRIVATE//私有小组
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public long getParGroupId() {
        return parGroupId;
    }

    public void setParGroupId(long parGroupId) {
        this.parGroupId = parGroupId;
    }

    public List<Long> getSubGroupIds() {
        return subGroupIds;
    }

    public void setSubGroupIds(List<Long> subGroupIds) {
        this.subGroupIds = subGroupIds;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
