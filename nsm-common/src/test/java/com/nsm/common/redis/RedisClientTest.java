package com.nsm.common.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * Created by nieshuming on 2018/6/2.
 */
public class RedisClientTest {

    public static void main(String[] args) {

        JedisCluster jc = RedisUtil.getJedisCluster();
        String reply = jc.setex("user/a", 40, "nsm");
        System.out.println("reply:" + reply);
        System.out.println("user/a:" + jc.get("user/a"));

        Jedis jedis = RedisUtil.geJedis();
        reply = jedis.setex("user/b", 40, "drift");
        System.out.println("reply:" + reply);
        System.out.println("user/b:" + jedis.get("user/b"));
    }
}
