package com.nsm.core.service;

import com.nsm.bean.ErrorCode;
import com.nsm.core.dao.UserDao;
import com.nsm.core.entity.User;
import com.nsm.core.exception.BusinessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by nieshuming on 2018/7/30
 */
@Service
public class AdminService {
    @Resource
    private UserDao userDao;

    /**
     * 检测用户权限并抛出异常
     * @param userId 用户Id
     * @param adminType 用户权限
     */
    private void checkPermissionAndThrowException(long userId, User.UserType adminType){
        User user = userDao.getUser(userId);
        if(user == null || user.getUserType() < adminType.ordinal()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
    }

    /**
     * 查询用户列表
     * @param uid
     * @param offset
     * @param limit
     * @param userType
     * @param status
     * @return
     */
    public List<User> userList(long uid, Integer offset, Integer limit, Integer userType, Integer status){
        checkPermissionAndThrowException(uid, User.UserType.ADMIN);
        return userDao.getUsers(offset, limit, userType, status);
    }

    /**
     * 禁用用户
     * @param uid
     * @param userId
     * @param forbid
     */
    public void forbid(long uid, long userId, boolean forbid){
        checkPermissionAndThrowException(uid, User.UserType.ADMIN);
        User user = userDao.getUser(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if(user.getUserType() >= User.UserType.ADMIN.ordinal()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        int newStatus = forbid ? User.UserStatus.FORBIDDEN.ordinal() : User.UserStatus.NORMAL.ordinal();
        if(user.getUserStatus() == newStatus) {
            return;
        }
        User.Update update = new User.Update(userId);
        update.userStatus = newStatus;
        userDao.updateUser(update);
    }

    /**
     * 设置用户为管理员
     * @param uid
     * @param userId
     * @param admin
     */
    public void setAdmin(long uid, long userId, boolean admin){
        checkPermissionAndThrowException(uid, User.UserType.SUPER_ADMIN);
        User user = userDao.getUser(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if(user.getUserType() >= User.UserType.SUPER_ADMIN.ordinal()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        int newUserType = admin ? User.UserType.ADMIN.ordinal() : User.UserType.NORMAL.ordinal();
        if(user.getUserType() == newUserType) {
            return;
        }
        User.Update update = new User.Update(userId);
        update.userType = newUserType;
        userDao.updateUser(update);
    }

}
