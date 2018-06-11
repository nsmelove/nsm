package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 用户小组
 *
 * Created by nsm on 2018/6/11
 */
public class UserGroup {
    private long userId;
    private long groupId;
    private String groupName;
    private int privacy;
    /**
     * 小组是否被禁言
     */
    boolean silent;
    private long createTime;

    public enum GroupPrivacy{
        OPEN,//公开小组
        CLOSED,//封闭小组
        PRIVATE//私有小组
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
