package com.nsm.common.redis;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by nieshuming on 2018/6/2
 */
public class RedisClientTest {

    public static void main(String[] args) {
        Supplier<JedisCluster> jedisSup = Suppliers.memoize(RedisUtil::getJedisCluster);
//        Jedis jd = RedisUtil.geJedis();
//        String reply = jd.setex("user/a", 40, "nsm");
//        System.out.println("reply:" + reply);
//        System.out.println("user/a:" + jd.get("user/a"));
//
//        Jedis jedis = RedisUtil.geJedis();
//        reply = jedis.setex("user/b", 40, "drift");
//        System.out.println("reply:" + reply);
//        System.out.println("user/b:" + jedis.get("user/b"));
        jedisSup.get().subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println("channel:" + channel + ",subscribedChannels:" + subscribedChannels);
            }
        }, "a");
        System.out.println("finished");
    }
}
