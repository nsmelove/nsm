package com.nsm.boot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.nsm.boot.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/9/19
 */
@Service
public class MessageService {
    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String getUserMsgTargetsKey(long userId){
        return "uMsgTargets:" + userId;
    }

    private String getUserMsgsKey(long userId, long targetId){
        return "uMsgs:" + Hashing.md5().hashString(userId +":" + targetId).toString().substring(8, 24);
    }

    private String getGroupMsgsKey(long groupId){
        return "gMsgs:" + groupId;
    }



    public List<Message> getUserMsgs(long userId, long targetId, int targetType, long lastId, int limit){
        String msgKey = null;
        if(targetType == Message.ToType.GROUP.ordinal()) {
            msgKey = getGroupMsgsKey(targetId);
        }else {
            msgKey = getUserMsgsKey(userId, targetId);
        }
        long scoreMax = lastId == 0 ? Long.MAX_VALUE : lastId - 1;
        Set<String> msgStrSet = template.opsForZSet().reverseRangeByScore(msgKey, scoreMax, 0, 0, limit);
        if(msgStrSet == null || msgStrSet.isEmpty()) {
            return Collections.emptyList();
        }else {
            List<Message> msgs = Lists.newArrayListWithExpectedSize(msgStrSet.size());
            for(String msgStr : msgStrSet) {
                try {
                    Message msg = objectMapper.readValue(msgStr, Message.class);
                    if(msg != null) {
                        msgs.add(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return msgs;
        }
    }
}
