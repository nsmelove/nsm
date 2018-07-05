package com.nsm.core.entity;

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

    private short groupLevel;

    private long creatorId;

    private String groupName;

    private int privacy;
    /**
     * 小组是否被禁言
     */
    boolean silent;

    private long parentId;

    private List<Long> subIds;

    private long createTime;

    public enum GroupPrivacy{
        OPEN,//公开小组
        CLOSED,//封闭小组
        PRIVATE//私有小组
    }

    public static class Update{
        public String groupName;
        public Integer privacy;
        public Boolean silent;
        public List<Long> addSubGIds;
        public List<Long> delSubGIds;

    }
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

    public List<Long> getSubIds() {
        return subIds;
    }

    public void setSubIds(List<Long> subIds) {
        this.subIds = subIds;
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
