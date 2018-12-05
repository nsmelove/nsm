package com.nsm.boot.controller;

import com.nsm.boot.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Created by nieshuming on 2018/9/21
 */
@Controller
public class MsgController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/toAll")
    @SendTo("/queue/toAll")
    @SendToUser("/queue/toAll")
    public String toAll(String msg, StompHeaderAccessor headerAccessor){
        System.out.println("receive msg:" + msg);
        System.out.println("headerAccessor:" + headerAccessor);
        template.convertAndSend("/other/toAll", msg);
        System.out.println(headerAccessor.getUser().getName());
        template.convertAndSendToUser("0", "/topic/toAll", msg);
        return msg;
    }
}
