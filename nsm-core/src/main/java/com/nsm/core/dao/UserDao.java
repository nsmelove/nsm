package com.nsm.core.dao;

import com.nsm.core.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/5/27
 */
@Repository("userDao")
public interface UserDao {

    void addUser(User user);

    User getUser(long uid);

    long countUser();

    User getUserByUsername(String username);

    List<User> getUsersByIds(@Param("uids") Collection<Long> uids);

    List<User> getUsers(@Param("offset") int offset, @Param("limit") int limit);

    void updateUser(User.Update update);

    void deleteUser(long uid);
}
