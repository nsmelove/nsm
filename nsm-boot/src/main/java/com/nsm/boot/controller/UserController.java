package com.nsm.boot.controller;

import com.google.common.collect.Maps;
import com.nsm.boot.entity.User;
import com.nsm.boot.pojo.Message;
import com.nsm.boot.repository.UserRepository;
import com.nsm.boot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by nieshuming on 2018/9/18
 */
@RestController
public class UserController {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MessageService messageService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/user")
    List<User> user() {
        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.DESC, "userId");
        //return userRepository.findAll(pageable).getContent();
        return jdbcTemplate.query("select * from user", (rs, rowNum) -> {
            User user = new User();
            user.setNickname(rs.getString("nickName"));
            return user;
        });
    }

    @RequestMapping("/msg")
    List<Message> msgs(){
        return messageService.getUserMsgs(1527524717296L, 1529916046421001L, 0, 0, 10);
    }
    @RequestMapping("/session")
    Object msgs(HttpServletRequest request){
        Enumeration<String> attrs = request.getSession().getAttributeNames();
        Map<String,Object> attrMap = Maps.newHashMap();
        while (attrs.hasMoreElements()){
            String name = attrs.nextElement();
            attrMap.put(name, request.getSession().getAttribute(name));
        }
        return attrMap;
    }
}
