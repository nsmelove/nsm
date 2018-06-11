package com.nsm.mvc.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
public class User {

    private long userId;
    private int userType;
    private String username;
    private String nickname;
    private String userIcon;
    private String password;
    private int userStatus;
    private long createTime;

    public static User newUpdate(){
        User update = new User();
        update.setUserType(-1);
        update.setUserStatus(-1);
        return update;
    }

    public enum UserType{
        NORMAL, ADMIN, SUPER_ADMIN
    }

    public enum UserStatus{
        NORMAL, FORBIDDEN
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
