package com.nsm.mvc.service;

import com.google.common.hash.Hashing;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.bean.UserSetting;
import com.nsm.mvc.cache.UserInfoCache;
import com.nsm.mvc.dao.UserDao;
import com.nsm.mvc.dao.UserSettingDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.view.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by nieshuming on 2018/6/11
 */
@Service
public class UserService {
    @Resource
    private AuthService authService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserSettingDao userSettingDao;
    @Resource
    private UserInfoCache userInfoCache;

    private String encodePwd(long userId, String password){
        String hashKey = userId + password;
        return Hashing.md5().hashString(hashKey).toString();
    }

    /**
     * 用户注册
     *
     * @param username
     * @param nickname
     * @param password
     * @return 用户Id
     */
    public long register(String username, String nickname, String password){
        long userId = IdUtils.nextLong();
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(encodePwd(userId, password));
        user.setCreateTime(System.currentTimeMillis());
        userDao.addUser(user);
        return userId;
    }

    /**
     * 用户登陆
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    public UserInfo login(String username, String password, String sid){
        User user = userDao.getUserByUsername(username);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_WRONG);
        }
        if(user.getUserStatus() != User.UserStatus.NORMAL.ordinal()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }
        String encodePwd = encodePwd(user.getUserId(), password);
        if(encodePwd.equals(user.getPassword())){
            Session session =authService.newSession(sid, user.getUserId(), user.getUserType());
            if(session == null) {
                throw new BusinessException(new ErrorCode(500,"登陆失败"));
            }
            return UserInfo.fromUser(user);
        }else {
            throw new BusinessException(ErrorCode.USER_PASSWORD_WRONG);
        }
    }


    public void rename(long userId, String name){
        User user = userDao.getUser(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(user.getUserStatus() != User.UserStatus.NORMAL.ordinal()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }
        if(!user.getNickname().equals(name)) {
            user.setNickname(name);
            userDao.updateUser(user);
            userInfoCache.delUserInfo(userId);
        }
    }

    public void changeIcon(long userId, String icon){
        //TODO
    }

    public void changePwd(long userId, String oldPwd, String newPwd){
        //TODO
    }

    public void changePrivacy(long userId, int privacy){
        User user = userDao.getUser(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }
        if(user.getUserStatus() != User.UserStatus.NORMAL.ordinal()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }
        if(user.getPrivacy() != privacy) {
            user.setPrivacy(privacy);
            userDao.updateUser(user);
            userInfoCache.delUserInfo(userId);
        }
    }

    public void changeSetting(UserSetting.Update update) {
        UserSetting setting = userSettingDao.getUserSetting(update.userId);
        if(setting != null) {
            setting = new UserSetting();
            setting.setUserId(update.userId);
            setting.setAutoJoinGroup(update.autoJoinGroup);
            userSettingDao.addUserSetting(setting);
        }else {
            User user = userDao.getUser(update.userId);
            if(user == null) {
                throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
            }
            userSettingDao.updateUserSetting(update);
        }
    }

    public UserInfo getUserInfo(long uid){
        UserInfo userInfo = userInfoCache.getUserInfo(uid);
        if (userInfo == null) {
            User user = userDao.getUser(uid);
            if (user != null) {
                userInfo = UserInfo.fromUser(user);
                userInfoCache.setUserInfo(userInfo);
            }
        }
        return userInfo;
    }

    public Map<Long, UserInfo> batchGetUserInfo(Collection<Long> uids){
        Map<Long, UserInfo> userInfoMap = userInfoCache.batchGetUserInfo(uids);
        List<Long> unCacheIds = uids.stream().filter(uid -> !userInfoMap.containsKey(uid)).collect(Collectors.toList());
        if(!unCacheIds.isEmpty()) {
            List<User> users = userDao.getUsersByIds(unCacheIds);
            for(User user : users) {
                if(user.getUserStatus() != User.UserStatus.FORBIDDEN.ordinal()) {
                    UserInfo userInfo = UserInfo.fromUser(user);
                    userInfoCache.setUserInfo(userInfo);
                    userInfoMap.put(userInfo.getUserId(),userInfo);
                }
            }
        }
        return userInfoMap;
    }

    public User getUser(long uid){
        return userDao.getUser(uid);
    }

    public List<User> getUsers(int offset, int limit){
        return userDao.getUsers(offset, limit);
    }

    public UserSetting getUserSetting(long userId){
        UserSetting setting = userSettingDao.getUserSetting(userId);
        if(setting == null) {
            User user = userDao.getUser(userId);
            if(user != null) {
                setting = new UserSetting();
                setting.setAutoJoinGroup(true);
            }
        }
        return setting;
    }

}
