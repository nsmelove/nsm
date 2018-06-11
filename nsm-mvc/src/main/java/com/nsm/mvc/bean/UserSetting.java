package com.nsm.mvc.bean;

/**
 * Description for this file
 *
 * Created by nsm on 2018/6/11.
 */
public class UserSetting {

    private long userId;
    private int privacy;
    private boolean autoJoinGroup;

    public enum UserPrivacy{
        PUBLIC,//所以人都能看到
        CONTACT,//联系人才能看到
        PRIVATE//自己才能看到
    }

}
