package com.nsm.core.dao;

import com.nsm.core.entity.UserSetting;
import org.springframework.stereotype.Repository;

/**
 * Created by nieshuming on 2018/6/19
 */
@Repository("userSettingDao")
public interface UserSettingDao {

    void addUserSetting(UserSetting setting);

    UserSetting getUserSetting(long userId);

    void updateUserSetting(UserSetting.Update update);

    void deleteUserSetting(long userId);
}
