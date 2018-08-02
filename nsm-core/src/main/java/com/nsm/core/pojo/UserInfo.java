package com.nsm.core.pojo;

import com.nsm.core.entity.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by nieshuming on 2018/6/11
 */
public class UserInfo {

    private long userId;
    private int userType;
    private String nickname;
    private String userIcon;
    private Long createTime;
    private int privacy;

    public static UserInfo fromUser(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUserType(user.getUserType());
        userInfo.setNickname(user.getNickname());
        userInfo.setUserIcon(user.getUserIcon());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setPrivacy(user.getPrivacy());
        return userInfo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
