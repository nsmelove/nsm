package com.nsm.common.memcache;

import com.nsm.common.conf.YamlConfigUtils;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
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
public class MemcachedUtil {
    private static final Logger logger = LoggerFactory.getLogger(MemcachedUtil.class);
    private static String confPath = "memcache.yaml";
    private static MemcachedClient client = null;

    public static MemcachedClient getMemClient(){
        if(client == null) {
            synchronized (MemcachedUtil.class) {
                if(client == null) {
                    MemcachedConfig config = YamlConfigUtils.loadConfig(confPath, MemcachedConfig.class);
                    List<String> servers = config.servers;
                    if(servers != null && !servers.isEmpty()) {
                        Map<InetSocketAddress, InetSocketAddress> addressMap = new HashMap<>(servers.size());
                        for (int i = 0 ; i < servers.size() ; i ++){
                            List<InetSocketAddress> masterSlave = AddrUtil.getAddresses(servers.get(i));
                            if(masterSlave.size() > 1) {
                                addressMap.put(masterSlave.get(0), masterSlave.get(1));
                            }else {
                                addressMap.put(masterSlave.get(0), null);
                            }
                        }
                        XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addressMap);
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
