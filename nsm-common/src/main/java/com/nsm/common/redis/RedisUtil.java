package com.nsm.common.redis;

import com.nsm.common.conf.YamlConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/2.
 */
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    public static Jedis geJedis(){
        return JedisPoolHolder.jedisPool.getResource();
    }

    public static Jedis geSentinelJedis(){
        return JedisSentinelPoolHolder.sentinelPool.getResource();
    }

    public static JedisCluster getJedisCluster(){
        return JedisClusterHolder.jc;
    }

    private static class ConfigHolder {
        private static RedisConfig config = YamlConfigUtils.loadConfig("redis.yaml", RedisConfig.class);
    }

    private static class JedisPoolHolder{
        private static JedisPool jedisPool = initPool();
        private static JedisPool initPool(){
            String server = ConfigHolder.config.server;
            HostAndPort hostAndPort = HostAndPort.parseString(server);
            return new JedisPool(hostAndPort.getHost(), hostAndPort.getPort());
        }

    }

    private static class JedisSentinelPoolHolder{
        private static JedisSentinelPool sentinelPool = initPool();
        private static JedisSentinelPool initPool(){
            String masterName = ConfigHolder.config.masterName;
            Set<String> sentinels = ConfigHolder.config.sentinels;
            return new JedisSentinelPool(masterName, sentinels);
        }

    }

    private static class JedisClusterHolder{
        private static JedisCluster jc = initJedisCluster();
        private static JedisCluster initJedisCluster(){
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            if(ConfigHolder.config.servers == null || ConfigHolder.config.servers.isEmpty()) {
                throw new IllegalArgumentException("no servers config find");
            }
            for(String server : ConfigHolder.config.servers) {
                HostAndPort hostAndPort = HostAndPort.parseString(server);
                jedisClusterNodes.add(hostAndPort);
            }
            return new JedisCluster(jedisClusterNodes);
        }
    }
}
