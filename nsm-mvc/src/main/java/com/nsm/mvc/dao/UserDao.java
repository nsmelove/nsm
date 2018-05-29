package com.nsm.mvc.dao;

import com.nsm.mvc.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2018/5/27.
 */
@Repository("userDao")
public interface UserDao {

    void addUser(User user);

    User getUser(long uid);

    List<User> getUsers(@Param("offset") int offset, @Param("limit")int limit);

    User getUserByUsername(String username);

}
