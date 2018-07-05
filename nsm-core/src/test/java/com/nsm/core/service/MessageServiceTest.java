package com.nsm.core.service;

import com.nsm.bean.message.Message;
import com.nsm.core.SpringContainer;

import java.util.List;

/**
 * Created by nieshuming on 2018/7/3
 */
public class MessageServiceTest {

    public static void main(String[] args) {

        MessageService messageService = SpringContainer.getBean(MessageService.class);
        List<Message> msgs = messageService.getUserMsgs(1527524717296L, 1529916046421001L, 0, 0, 100);
        msgs.forEach(System.out::println);
        System.exit(0);
    }
}
