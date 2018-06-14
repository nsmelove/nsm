package com.nsm.common.redis;

import java.util.List;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/2.
 */
public final class RedisConfig {

    /**
     * 单节点服务地址
     */
    public String server;

    /**
     * 哨兵系统服务地址
     */
    public Set<String> sentinels;

    /**
     * 哨兵系统主节点名称
     */
    public String masterName;
    /**
     * 集群服务地址
     */
    public List<String> servers;
}
