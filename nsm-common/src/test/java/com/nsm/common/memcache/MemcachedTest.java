package com.nsm.common.memcache;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Created by nieshuming on 2018/6/2
 */
public class MemcachedTest {

    public static void main(String[] args) {

        MemcachedClient client = MemcachedUtil.getMemClient();
        try {
            boolean result = client.set("user/a",40, "nsm1");
            System.out.println("result:" + result);
            System.out.println("user/a:" + client.get("user/a"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
