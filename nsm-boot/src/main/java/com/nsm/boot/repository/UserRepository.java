package com.nsm.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsm.boot.entity.User;
/**
 * Created by nieshuming on 2018/9/19
 */
public interface UserRepository extends JpaRepository<User, Long> {

}
