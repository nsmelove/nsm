package com.nsm.common.redis;

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
    private static volatile Jedis jedis = null;
    private static volatile JedisCluster jc = null;

    public static Jedis geJedis(){
        if(jedis == null) {
            synchronized (Jedis.class){
                if(jedis == null) {
                    String server = RedisConfig.config.server;
                    if(server == null) {
                        throw new IllegalArgumentException("no server config find");
                    }
                    int spIdx = server.lastIndexOf(":");
                    if(spIdx == -1) {
                        throw new IllegalArgumentException("host address '"+ server + "'illegal");
                    }
                    String hostPart = server.substring(0, spIdx).trim();
                    int portNum = Integer.valueOf(server.substring(spIdx + 1).trim());
                    jedis = new Jedis(hostPart, portNum);
                    Runtime.getRuntime().addShutdownHook(new Thread(){
                        @Override
                        public void run() {
                            jedis.shutdown();
                        }
                    });
                }
            }
        }
        return jedis;
    }

    public static JedisCluster getJedisCluster(){
        if (jc == null) {
            synchronized (JedisCluster.class){
                if(jc == null){
                    Set<HostAndPort> jedisClusterNodes = new HashSet<>();
                    RedisConfig config = RedisConfig.config;
                    if(config.servers == null || config.servers.isEmpty()) {
                        throw new IllegalArgumentException("no servers config find");
                    }
                    for(String server : config.servers) {
                        int spIdx = server.lastIndexOf(":");
                        if(spIdx == -1) {
                            throw new IllegalArgumentException("host address '"+ server + "'illegal");
                        }
                        String hostPart = server.substring(0, spIdx).trim();
                        int portNum = Integer.valueOf(server.substring(spIdx + 1).trim());
                        HostAndPort hostAndPort = new HostAndPort(hostPart,portNum);
                        jedisClusterNodes.add(hostAndPort);
                    }
                    jc = new JedisCluster(jedisClusterNodes);
                }
            }
        }
        return jc;
    }
}
