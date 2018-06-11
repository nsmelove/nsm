package com.nsm.mvc.service;

import com.google.common.hash.Hashing;
import com.nsm.common.utils.IdUtils;
import com.nsm.mvc.bean.Session;
import com.nsm.mvc.bean.User;
import com.nsm.mvc.cache.UserInfoCache;
import com.nsm.mvc.dao.UserDao;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import com.nsm.mvc.view.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by nieshuming on 2018/6/11.
 */
@Service
public class UserService {
    @Resource
    private AuthService authService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserInfoCache userInfoCache;

    private String encodePwd(long userId, String password){
        String hashKey = userId + password;
        return Hashing.md5().hashString(hashKey).toString();
    }

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
    public User login(String username, String password, String sid){
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
            return user;
        }else {
            throw new BusinessException(ErrorCode.USER_PASSWORD_WRONG);
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

    public Map<Long, UserInfo> getUserInfos(Collection<Long> uids){
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
        List<User> users = userDao.getUsers(offset, limit);
        return users;
    }
}
