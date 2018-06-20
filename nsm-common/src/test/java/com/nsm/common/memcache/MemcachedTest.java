package com.nsm.common.memcache;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Created by nieshuming on 2018/6/2
 */
public class MemcachedTest {

    public static void main(String[] args) {

        MemcachedClient client = MemcachedUtil.getMemClient();
        try {
            boolean result = client.set("user/a",4, "nsm2");
            System.out.println("result:" + result);
            client.incr("a",1,0,3000,4);
            while (true){
                System.out.println("user/a:" + client.get("user/a"));
                System.out.println("a:" + client.incr("a",1,0,3000,4));
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
