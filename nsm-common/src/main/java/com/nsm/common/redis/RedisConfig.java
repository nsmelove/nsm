package com.nsm.common.redis;

import java.util.List;

/**
 * Created by nieshuming on 2018/6/2.
 */
public final class RedisConfig {

    /**
     * 单节点服务地址
     */
    public String server;

    /**
     * 集群服务地址
     */
    public List<String> servers;
}
