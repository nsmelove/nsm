package com.nsm.mvc.service;

import com.nsm.common.memcache.MemcachedUtil;
import com.nsm.common.utils.JsonUtils;
import com.nsm.mvc.bean.Session;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Created by nsm on 2018/6/3
 */
@Service
public class AuthService {
    private Logger logger = Logger.getLogger(this.getClass());
    private MemcachedClient memClient = MemcachedUtil.getMemClient();

    public Session newSession(String sid, long uid){
        Session session = new Session();
        session.setSessionId(sid);
        session.setUserId(uid);
        String jsonSession = JsonUtils.toJson(session);
        if(jsonSession != null) {
            try {
                memClient.set(sid,0,session);
                return session;
            } catch (Exception e) {
                logger.error("save session error", e);
            }
        }
        return null;
    }

    public Session getSession(String sid){
        try {
            String jsonSession = memClient.get(sid);
            if (jsonSession != null) {
                return JsonUtils.toObject(jsonSession, Session.class);
            }
        } catch (Exception e) {
            logger.error("get session error", e);
        }
        return null;
    }
}
