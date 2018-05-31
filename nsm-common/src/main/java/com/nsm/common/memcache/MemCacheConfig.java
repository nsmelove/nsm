package com.nsm.common.memcache;

import com.nsm.common.utils.YamlConfigUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/5/31.
 */
public class MemCacheConfig {
    private static String confPath = "memcache.yaml";
    public static MemCacheConfig conf = YamlConfigUtils.loadConfig(confPath, MemCacheConfig.class);


    public List<String> servers;



    public static void main(String[] args) {
        System.out.println(MemCacheConfig.conf.servers);
    }
}
