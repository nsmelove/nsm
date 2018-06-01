package com.nsm.common.memcache;

import com.nsm.common.conf.YamlConfigUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/5/31.
 */
public class MemcachedConfig{
    private static MemcachedConfig config;
    private static String confPath = "memcache.yaml";
    public static MemcachedConfig config(){
        if(config == null) {
            config = YamlConfigUtils.getConfig(MemcachedConfig.class, confPath);
        }
        return config;
    }

    public List<String> servers;



    public static void main(String[] args) {
        MemcachedConfig config = MemcachedConfig.config();
        //System.out.println(MemcachedConfig.conf.servers);
    }
}
