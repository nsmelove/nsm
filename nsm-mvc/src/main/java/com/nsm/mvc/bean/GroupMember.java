package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 小组成员
 *
 * @author Created by nsm on 2018/6/11.
 */
public class GroupMember {

    long groupId;
    long memberId;
    boolean isAdmin;
    /**
     * 是否被禁言
     */
    boolean silent;
    private long joinTime;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
