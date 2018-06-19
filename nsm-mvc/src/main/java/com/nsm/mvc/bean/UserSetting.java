package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Description for this file
 *
 * Created by nsm on 2018/6/11.
 */
public class UserSetting {

    private long userId;
    private boolean autoJoinGroup;

    public static class Update{
        public long userId;
        public Boolean autoJoinGroup;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isAutoJoinGroup() {
        return autoJoinGroup;
    }

    public void setAutoJoinGroup(boolean autoJoinGroup) {
        this.autoJoinGroup = autoJoinGroup;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
