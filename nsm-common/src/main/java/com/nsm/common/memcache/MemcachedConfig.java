package com.nsm.common.memcache;

import com.nsm.common.conf.YamlConfigUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/5/31.
 */
public final class MemcachedConfig{
    private static String confFile = "memcache.yaml";
    protected static MemcachedConfig config = YamlConfigUtils.loadConfig(confFile, MemcachedConfig.class);

    public List<String> servers;

    public long connectTimeout;

}
