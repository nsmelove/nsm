package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/18.
 */
public class GroupInvite {
    private long groupId;
    private long inviterId;
    private long invitTime;
    private long inviteeId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getInviterId() {
        return inviterId;
    }

    public void setInviterId(long inviterId) {
        this.inviterId = inviterId;
    }

    public long getInvitTime() {
        return invitTime;
    }

    public void setInvitTime(long invitTime) {
        this.invitTime = invitTime;
    }

    public long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(long inviteeId) {
        this.inviteeId = inviteeId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
