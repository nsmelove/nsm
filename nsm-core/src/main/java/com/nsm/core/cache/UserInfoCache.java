package com.nsm.core.cache;

import com.google.common.collect.Maps;
import com.nsm.common.memcache.MemcachedUtil;
import com.nsm.common.utils.JsonUtils;
import com.nsm.core.pojo.UserInfo;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by nieshuming on 2018/6/11
 */
@Component
public class UserInfoCache {
    private Logger logger = Logger.getLogger(this.getClass());
    private MemcachedClient memcachedClient = MemcachedUtil.getMemClient();

    private String getUserInfoKey(long uid) {
        return new StringBuilder("uInfo:").append(uid).toString();
    }

    public UserInfo getUserInfo(long uid) {
        String key = getUserInfoKey(uid);
        UserInfo userInfo = null;
        try {
            String value = memcachedClient.get(key);
            if(value != null) {
                userInfo = JsonUtils.toObject(value, UserInfo.class);
            }
        } catch (Exception e) {
            logger.error("get user info cache error", e);
        }
        return userInfo;
    }

    public Map<Long, UserInfo> batchGetUserInfo(Collection<Long> uids) {
        Map<Long, UserInfo> userInfoMap = Maps.newHashMapWithExpectedSize(uids.size());
        Map<String,Long> keyIdMap = uids.stream().collect(Collectors.toMap(this::getUserInfoKey, Function.identity()));
        try {
            Map<String,String>  keyValueMap = memcachedClient.get(keyIdMap.keySet());
            keyValueMap.forEach((key, value) ->{
                UserInfo userInfo = JsonUtils.toObject(value, UserInfo.class);
                userInfoMap.put(keyIdMap.get(key), userInfo);
            });
        } catch (Exception e) {
            logger.error("batch get user info cache error", e);
        }
        return userInfoMap;
    }

    public void setUserInfo(UserInfo userInfo) {
        String key = getUserInfoKey(userInfo.getUserId());
        String value = JsonUtils.toJson(userInfo);
        if(value != null) {
            try {
                memcachedClient.set(key, 0 , value);
            } catch (Exception e) {
                logger.error("set user info cache error", e);
            }
        }
    }

    public void delUserInfo(long uid) {
        String key = getUserInfoKey(uid);
        try {
            memcachedClient.delete(key);
        } catch (Exception e) {
            logger.error("delete user info cache error", e);
        }
    }
}
