package com.nsm.common.redis;

import com.nsm.common.conf.YamlConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/2.
 */
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    public static Jedis geJedis(){
        return JedisHolder.jedis;
    }

    public static JedisCluster getJedisCluster(){
        return JedisClusterHolder.jc;
    }

    private static class ConfigHolder {
        private static RedisConfig config = YamlConfigUtils.loadConfig("redis.yaml", RedisConfig.class);
    }

    private static class JedisHolder{
        private static Jedis jedis = initJedis();
        private static Jedis initJedis(){
            String server = ConfigHolder.config.server;
            if(server == null) {
                throw new IllegalArgumentException("no server config find");
            }
            int spIdx = server.lastIndexOf(":");
            if(spIdx == -1) {
                throw new IllegalArgumentException("host address '"+ server + "'illegal");
            }
            String hostPart = server.substring(0, spIdx).trim();
            int portNum = Integer.valueOf(server.substring(spIdx + 1).trim());
            return new Jedis(hostPart, portNum);
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
                int spIdx = server.lastIndexOf(":");
                if(spIdx == -1) {
                    throw new IllegalArgumentException("host address '"+ server + "'illegal");
                }
                String hostPart = server.substring(0, spIdx).trim();
                int portNum = Integer.valueOf(server.substring(spIdx + 1).trim());
                HostAndPort hostAndPort = new HostAndPort(hostPart,portNum);
                jedisClusterNodes.add(hostAndPort);
            }
            return new JedisCluster(jedisClusterNodes);
        }
    }
}
