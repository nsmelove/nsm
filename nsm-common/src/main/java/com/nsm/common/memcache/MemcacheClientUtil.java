package com.nsm.common.memcache;

import com.google.code.yanf4j.config.Configuration;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/31.
 */
public class MemcacheClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(MemcacheClientUtil.class);
    private static MemcachedClient client = null;

    public static MemcachedClient getMemClient(){
        if(client == null) {
            synchronized (MemcacheClientUtil.class) {
                if(client == null) {
                    List<String> servers = MemCacheConfig.conf.servers;
                    if(servers != null && !servers.isEmpty()) {
                        Map<InetSocketAddress, InetSocketAddress> addressMap = new HashMap<>(servers.size()/2 + 1);
                        for (int i = 0 ; i < servers.size() ; i += 2){
                            InetSocketAddress master = null;
                            InetSocketAddress slave = null;
                            String [] hostPort = servers.get(i).split(":");
                            if(hostPort != null && hostPort.length == 2) {
                                master = new InetSocketAddress(hostPort[0],Integer.valueOf(hostPort[1]));
                            }
                            if(i + 1 < servers.size()){
                                hostPort = servers.get(i + 1).split(":");
                                if(hostPort != null && hostPort.length == 2) {
                                    if(master == null) {
                                        master = new InetSocketAddress(hostPort[0],Integer.valueOf(hostPort[1]));
                                    } else {
                                        slave = new InetSocketAddress(hostPort[0],Integer.valueOf(hostPort[1]));
                                    }
                                }
                            }
                            if(master != null) {
                                addressMap.put(master, slave);
                            }
                        }
                        XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addressMap);
                        builder.setConnectTimeout(3000);
                        try {
                            client = builder.build();
                        } catch (IOException e) {
                            logger.error("get memcache client err" , e);
                        }
                    }
                }
            }
        }
        return client;
    }
}
