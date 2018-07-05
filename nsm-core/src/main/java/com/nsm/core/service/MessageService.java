package com.nsm.core.service;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.nsm.common.redis.RedisUtil;
import com.nsm.common.utils.JsonUtils;
import com.nsm.bean.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/25
 */
@Service
public class MessageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private UserGroupService userGroupService;
    private JedisCluster jedis  = RedisUtil.getJedisCluster();

    private String getUserMsgTargetsKey(long userId){
        return "uMsgTargets:" + userId;
    }

    private String getUserMsgsKey(long userId, long targetId){
        return "uMsgs:" + Hashing.md5().hashString(userId +":" + targetId).toString().substring(8, 24);
    }

    private String getGroupMsgsKey(long groupId){
        return "gMsgs:" + groupId;
    }

    public void addMsg(Message msg){
        double score = msg.getMessageId();
        String msgStr = JsonUtils.toJson(msg);
        if(msg.getToType() == Message.ToType.GROUP.ordinal()) {
            String groupMsgKey = getGroupMsgsKey(msg.getToId());
            jedis.zadd(groupMsgKey, score, msgStr);
            List<Long> memberIds = userGroupService.groupMemberIdList(msg.getToId());
            for(long memberId : memberIds) {
                String uTargetsKey = getUserMsgTargetsKey(memberId);
                jedis.hset(uTargetsKey, String.valueOf(msg.getToId()), msgStr);
            }
        }else {
            String fromMsgsKey = getUserMsgsKey(msg.getFromId(), msg.getToId());
            jedis.zadd(fromMsgsKey, score, msgStr);
            String toMsgsKey = getUserMsgsKey(msg.getToId(), msg.getFromId());
            jedis.zadd(toMsgsKey, score, msgStr);
            String fromTargetsKey = getUserMsgTargetsKey(msg.getFromId());
            jedis.hset(fromTargetsKey, String.valueOf(msg.getToId()), msgStr);
            String toTargetsKey = getUserMsgTargetsKey(msg.getToId());
            jedis.hset(toTargetsKey, String.valueOf(msg.getFromId()), msgStr);
        }
        //TODO 缓存失效,存数据库
    }

    public List<Message> getUserMsgs(long userId, long targetId, int targetType, long lastId, int limit){
        String msgKey = null;
        if(targetType == Message.ToType.GROUP.ordinal()) {
            msgKey = getGroupMsgsKey(targetId);
        }else {
            msgKey = getUserMsgsKey(userId, targetId);
        }
        long scoreMax = lastId == 0 ? Long.MAX_VALUE : lastId - 1;
        Set<String> msgStrs = jedis.zrevrangeByScore(msgKey, scoreMax, 0, 0, limit);
        if(msgStrs == null || msgStrs.isEmpty()) {
            return Collections.emptyList();
        }else {
            List<Message> msgs = Lists.newArrayListWithExpectedSize(msgStrs.size());
            for(String msgStr : msgStrs) {
                Message msg = JsonUtils.toObject(msgStr, Message.class);
                if(msg != null) {
                    msgs.add(msg);
                }
            }
            return msgs;
        }
    }
}
