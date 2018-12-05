package com.nsm.core.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.common.redis.RedisUtil;
import com.nsm.core.entity.Session;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by nsm on 2018/6/3
 */
@Service
public class SessionService {
    private Logger logger = Logger.getLogger(this.getClass());
    private final int SESSION_EXP_TIME = 30 * 24 * 3600;
    //private final String FIELD_SESSIONID = "sessionId";
    private final String FIELD_USERID = "userId";
    //private final String FIELD_LASTTIME = "lastTime";

    //private Jedis jedis  = RedisUtil.geJedis();
    private JedisCluster jedis  = RedisUtil.getJedisCluster();
    //TODO 可设置二级缓存
//    private LoadingCache<String, Session> loadingCache =
//            CacheBuilder.newBuilder().build(new CacheLoader<String, Session>() {
//                @Override
//                templates Session load(String sessionId) throws Exception {
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

    /**
     * 获取会话对应的用户Id
     * @param sid
     * @return
     */
    public long getUserId(String sid){
        String sidKey = getSessionKey(sid);
        String uidStr = jedis.hget(sidKey, FIELD_USERID);
        if(uidStr == null) {
            return 0L;
        }
        return Long.valueOf(uidStr);
    }

    /**
     * 获取用户的所有会话Id
     * @param userId
     * @return
     */
    public Set<String> getUserSessionIds(long userId) {
        String userSidsKey = getUserSessionsKey(userId);
        Set<Tuple> sidScores = jedis.zrangeWithScores(userSidsKey, 0, Long.MAX_VALUE);
        if(sidScores == null) {
            return Collections.emptySet();
        }
        long now = System.currentTimeMillis();
        Set<String> sids = Sets.newLinkedHashSetWithExpectedSize(sidScores.size());
        for(Tuple sidScore : sidScores) {
            if(now - sidScore.getScore() > SESSION_EXP_TIME * 1000L) {
                jedis.zrem(userSidsKey, sidScore.getElement());
            }else {
                sids.add(sidScore.getElement());
            }
        }
        return sids;
    }

    /**
     * 获取用户所有的话
     * @param userId
     * @return
     */
    public Set<Session> getUserSessions(long userId) {
        Set<String> sids = getUserSessionIds(userId);
        if(sids.isEmpty()) {
            return Collections.emptySet();
        }
        String userSidsKey = getUserSessionsKey(userId);
        Set<Session> sessions = Sets.newLinkedHashSetWithExpectedSize(sids.size());
        for(String sid : sids) {
            Session session = getSession(sid);
            if(session == null || session.getUserId() != userId) {
                jedis.zrem(userSidsKey, sid);
            }else {
                sessions.add(session);
            }
        }
        return sessions;
    }

    /**
     * 刷新会话，防止过期
     * @param sid 会话Id
     * @return 会话存在true 否则false
     */
    public boolean refreshSession(String sid){
        long userId = getUserId(sid);
        if(userId > 0) {
            String sidKey = getSessionKey(sid);
            jedis.expire(sidKey, SESSION_EXP_TIME);
            String userSidsKey = getUserSessionsKey(userId);
            jedis.zadd(userSidsKey, System.currentTimeMillis() ,sid);
            jedis.expire(userSidsKey, SESSION_EXP_TIME);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 清除会话
     * @param userId 用户Id
     * @param sid 会话Id
     * @return 结果
     */
    public boolean remSession(long userId, String sid){
        if(userId <= 0) {
            return false;
        }
        String sidKey = getSessionKey(sid);
        jedis.del(sidKey);
        String userSidsKey = getUserSessionsKey(userId);
        jedis.zrem(userSidsKey, sid);
        return true;
    }

    /**
     * 清除会话
     * @param sid 会话Id
     * @return 结果
     */
    public boolean remSession(String sid){
        long userId = getUserId(sid);
        return remSession(userId, sid);
    }
}
