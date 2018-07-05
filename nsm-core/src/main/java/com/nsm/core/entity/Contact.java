package com.nsm.core.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 联系人对象
 *
 * Created by nieshuming on 2018/7/3
 */
public class Contact {
    private long userFrom;
    private long userTo;
    private long createTime;

    public static Contact newContact(long userFrom, long userTo){
        Contact contact = new Contact();
        contact.setUserFrom(userFrom);
        contact.setUserTo(userTo);
        contact.setCreateTime(System.currentTimeMillis());
        return contact;
    }
    public long getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(long userFrom) {
        this.userFrom = userFrom;
    }

    public long getUserTo() {
        return userTo;
    }

    public void setUserTo(long userTo) {
        this.userTo = userTo;
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
