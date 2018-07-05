package com.nsm.core.pojo;

import com.nsm.core.entity.Contact;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by nieshuming on 2018/7/5
 */
public class ContactInfo {

    private long contactId;
    private String nickname;
    private String userIcon;
    private long createTime;

    public static ContactInfo copyUserInfo(UserInfo userInfo){
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setContactId(userInfo.getUserId());
        contactInfo.setNickname(userInfo.getNickname());
        contactInfo.setUserIcon(userInfo.getUserIcon());
        return contactInfo;
    }
    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
