package com.nsm.mvc.service;

import com.google.common.collect.Maps;
import com.nsm.common.memcache.MemcachedUtil;
import com.nsm.common.redis.RedisUtil;
import com.nsm.common.utils.JsonUtils;
import com.nsm.mvc.bean.Session;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

/**
 * Created by nsm on 2018/6/3
 */
@Service
public class AuthService {
    private Logger logger = Logger.getLogger(this.getClass());
    private final int SESSION_EXP_TIME = 30 * 24 * 3600;
    //private final String FIELD_SESSIONID = "sessionId";
    private final String FIELD_USERID = "userId";
    //private final String FIELD_LASTTIME = "lastTime";

    private Jedis jedis  = RedisUtil.geJedis();
    private MemcachedClient memClient = MemcachedUtil.getMemClient();
//    private LoadingCache<String, Session> loadingCache =
//            CacheBuilder.newBuilder().build(new CacheLoader<String, Session>() {
//                @Override
//                public Session load(String sessionId) throws Exception {
//                    return null;
//                }
//            });

    private String getUserSessionsKey(long userId){
        return "uSids:" + userId;
    }

    private String getSessionKey(String sessionId){
        return "sid:" + sessionId;
    }

    /**
     * 新建会话
     * @param sid 会话Id
     * @param uid 用户Id
     * @return 会话
     */
    public Session newSession(String sid, long uid){
        long now = System.currentTimeMillis();
        Session session = new Session();
        session.setSessionId(sid);
        session.setUserId(uid);

        String sidKey = getSessionKey(sid);
        Map<String,String> fieldValueMap = Maps.newHashMap();
        fieldValueMap.put(FIELD_USERID, String.valueOf(session.getUserId()));
        jedis.hmset(sidKey, fieldValueMap);
        jedis.expire(sidKey, SESSION_EXP_TIME);

        String userSidsKey = getUserSessionsKey(uid);
        jedis.zadd(userSidsKey, now, sid);
        jedis.expire(userSidsKey, SESSION_EXP_TIME);
        return session;
    }

    /**
     * 获取会话
     * @param sid 会话Id
     * @return 会话
     */
    public Session getSession(String sid){
        String sidKey = getSessionKey(sid);
        Map<String, String> filedValues = jedis.hgetAll(sidKey);
        if(filedValues == null) {
            return null;
        }
        Session session = new Session();
        session.setSessionId(sid);
        session.setUserId(Long.valueOf(filedValues.get(FIELD_USERID)));
        return session;
    }

    public long getUserId(String sid){
        String sidKey = getSessionKey(sid);
        String uidStr = jedis.hget(sidKey,FIELD_USERID);
        if(uidStr == null) {
            return 0L;
        }
        return Long.valueOf(uidStr);
    }

    public Set<String> getUserSessionIds(long userId) {
        String userSidsKey = getUserSessionsKey(userId);
        Set<String> sids = jedis.zrange(userSidsKey, 0, Long.MAX_VALUE);
        //TODO
        return sids;
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
