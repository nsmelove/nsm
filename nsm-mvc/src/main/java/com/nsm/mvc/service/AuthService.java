package com.nsm.mvc.service;

import com.nsm.common.memcache.MemcachedUtil;
import com.nsm.common.redis.RedisUtil;
import com.nsm.common.utils.JsonUtils;
import com.nsm.mvc.bean.Session;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCommands;

/**
 * Created by nsm on 2018/6/3
 */
@Service
public class AuthService {
    private Logger logger = Logger.getLogger(this.getClass());
    private MemcachedClient memClient = MemcachedUtil.getMemClient();
    private JedisCommands jedisCommands  = RedisUtil.getJedisCluster();

    private String getUserSessionsKey(long userId){
        return new StringBuilder("uSids:").append(userId).toString();
    }

    /**
     * 新建会话
     * @param sid 会话Id
     * @param uid 用户Id
     * @return 会话
     */
    public Session newSession(String sid, long uid){
        Session session = new Session();
        session.setSessionId(sid);
        session.setUserId(uid);
        String jsonSession = JsonUtils.toJson(session);
        if(jsonSession != null) {
            try {
                String userSessionsKey = getUserSessionsKey(uid);
                memClient.set(sid,0,jsonSession);
                jedisCommands.sadd(userSessionsKey, sid);
                //TODO
                return session;
            } catch (Exception e) {
                logger.error("save session error", e);
            }
        }
        return null;
    }

    /**
     * 获取会话
     * @param sid 会话Id
     * @return 会话
     */
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

    /**
     * 更新会话
     * @param session 会话
     * @return 结果
     */
    public boolean updateSession(Session session){
        if(session == null && StringUtils.isEmpty(session.getSessionId())) {
            return false;
        }
        String jsonSession = JsonUtils.toJson(session);
        try {
            memClient.set(session.getSessionId(), 0, jsonSession);
            return true;
        } catch (Exception e) {
            logger.error("update session error", e);
            return false;
        }
    }

    /**
     * 清除会话
     * @param sid 会话Id
     * @return 结果
     */
    public boolean remSession(String sid){
        try {
            memClient.delete(sid);
            return true;
        }catch (Exception e) {
            logger.error("remove session error", e);
            return false;
        }

    }
}
