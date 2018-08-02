package com.nsm.core.pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * 用户小组信息
 *
 * Created by nsm on 2018/6/11
 */
public class GroupInfo {

    private long groupId;

    private String groupName;

    private short groupLevel;

    private long creatorId;

    private long creatorName;

    private int privacy;
    /**
     * 小组是否被禁言
     */
    boolean silent;

    private long parentId;

    private List<GroupInfo> subGroups;

    private long createTime;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public short getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(short groupLevel) {
        this.groupLevel = groupLevel;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public long getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(long creatorName) {
        this.creatorName = creatorName;
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<GroupInfo> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<GroupInfo> subGroups) {
        this.subGroups = subGroups;
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
