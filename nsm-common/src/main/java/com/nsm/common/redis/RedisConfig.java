package com.nsm.common.redis;

import com.nsm.common.conf.YamlConfigUtils;

import java.util.List;

/**
 * Created by nieshuming on 2018/6/2.
 */
public final class RedisConfig {
    private static String confFile= "redis.yaml";
    protected static RedisConfig config = YamlConfigUtils.loadConfig(confFile, RedisConfig.class);

    /**
     * 单节点服务地址
     */
    public String server;

    /**
     * 集群服务地址
     */
    public List<String> servers;
}
