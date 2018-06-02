package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Administrator on 2018/6/2
 */
public class Session {
    private String sessionId;
    private long userId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
