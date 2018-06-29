package com.nsm.websocket.bean;

import com.google.common.collect.Sets;
import io.vertx.core.shareddata.AsyncMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/26
 */
public class Receiver {
    private long userId;
    /**
     * 用户对应哪些session接收消息，为空表示所有用户都接收
     */
    private Set<String> sessionIds;
    /**
     * 用户的哪些session不接收消息
     */
    private Set<String> ignoreSids;

    public static Receiver newReceiver(long userId){
        Receiver receiver = new Receiver();
        receiver.setUserId(userId);
        return receiver;
    }

    public static Receiver newReceiver(long userId, Collection<String> sessionIds){
        Receiver receiver = new Receiver();
        receiver.setUserId(userId);
        if(sessionIds != null){
            receiver.setSessionIds(Sets.newHashSet(sessionIds));
        }
        return receiver;
    }

    public static Receiver newReceiverIgnore(long userId, Collection<String> ignoreSids){
        Receiver receiver = new Receiver();
        receiver.setUserId(userId);
        if(ignoreSids != null){
            receiver.setIgnoreSids(Sets.newHashSet(ignoreSids));
        }
        return receiver;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Set<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public Receiver addToSessionIds(String sessionId){
        if(sessionId != null) {
            if (this.sessionIds == null) {
                this.sessionIds = Sets.newHashSet();
            }
            this.sessionIds.add(sessionId);
        }
        return this;
    };

    public Receiver addToSessionIds(Collection<String> sessionIds){
        if(sessionIds != null && !sessionIds.isEmpty()) {
            if (this.sessionIds == null) {
                this.sessionIds = Sets.newHashSet();
            }
            this.sessionIds.addAll(sessionIds);
        }
        return this;
    };

    public Set<String> getIgnoreSids() {
        return ignoreSids;
    }

    public void setIgnoreSids(Set<String> ignoreSids) {
        this.ignoreSids = ignoreSids;
    }

    public Receiver addToIgnoreSids(String ignoreSid){
        if(ignoreSid != null) {
            if (this.ignoreSids == null) {
                this.ignoreSids = Sets.newHashSet();
            }
            this.ignoreSids.add(ignoreSid);
        }
        return this;
    };

    public Receiver addToIgnoreSids(Collection<String> ignoreSids){
        if(ignoreSids != null && !ignoreSids.isEmpty()) {
            if (this.ignoreSids == null) {
                this.ignoreSids = Sets.newHashSet();
            }
            this.ignoreSids.addAll(ignoreSids);
        }
        return this;
    };

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
