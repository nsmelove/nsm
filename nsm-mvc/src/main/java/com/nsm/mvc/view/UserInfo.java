package com.nsm.mvc.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsm.mvc.bean.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by nieshuming on 2018/6/11.
 */
public class UserInfo {

    public static UserInfo fromUser(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setNickname(user.getNickname());
        userInfo.setUserIcon(user.getUserIcon());
        userInfo.setCreateTime(user.getCreateTime());
        return userInfo;
    }

    private long userId;
    private String nickname;
    private String userIcon;
    private Long createTime;
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
