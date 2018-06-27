package com.nsm.core.service;

import com.nsm.core.SpringContainer;

/**
 * Created by nieshuming on 2018/6/25.
 */
public class AuthServiceTest {

    public static void main(String[] args) {
        SessionService sessionService = SpringContainer.getBean(SessionService.class);
        sessionService.getUserSessions(1529916046421001L).forEach(System.out::println);
    }
}
