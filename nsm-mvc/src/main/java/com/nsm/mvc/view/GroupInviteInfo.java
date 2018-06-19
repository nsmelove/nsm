package com.nsm.mvc.view;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/18.
 */
public class GroupInviteInfo {

    private long groupId;
    private String groupName;
    private long inviterId;
    private String inviterName;
    private String inviterIcon;
    private long inviteTime;
    private long inviteeId;
    private String inviteeName;
    private String inviteeIcon;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getInviterId() {
        return inviterId;
    }

    public void setInviterId(long inviterId) {
        this.inviterId = inviterId;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviterIcon() {
        return inviterIcon;
    }

    public void setInviterIcon(String inviterIcon) {
        this.inviterIcon = inviterIcon;
    }

    public long getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(long inviteTime) {
        this.inviteTime = inviteTime;
    }

    public long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public String getInviteeIcon() {
        return inviteeIcon;
    }

    public void setInviteeIcon(String inviteeIcon) {
        this.inviteeIcon = inviteeIcon;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
