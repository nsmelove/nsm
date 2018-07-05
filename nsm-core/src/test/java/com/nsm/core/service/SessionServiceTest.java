package com.nsm.core.service;

import com.nsm.core.SpringContainer;
import com.nsm.core.entity.Session;

import java.util.Set;

/**
 * Created by nieshuming on 2018/7/5
 */
public class SessionServiceTest {

    public static void main(String[] args) {
        SessionService sessionService = new SessionService();
        long userId = 1527524717296L;
        Set<Session> sessions = sessionService.getUserSessions(userId);
        System.out.println("user:" + userId + " sessions");
        sessions.forEach(System.out::println);
    }
}

