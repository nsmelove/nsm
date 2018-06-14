package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * 用户小组
 *
 * Created by nsm on 2018/6/11
 */
public class UserGroup {

    private long groupId;

    private long creatorId;

    private String groupName;

    private int privacy;
    /**
     * 小组是否被禁言
     */
    boolean silent;

    private List<UserGroup> subGroups;

    private List<Long> memberIds;

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

    public List<UserGroup> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<UserGroup> subGroups) {
        this.subGroups = subGroups;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
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
