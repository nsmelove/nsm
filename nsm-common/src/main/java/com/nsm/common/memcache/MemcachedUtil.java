package com.nsm.common.memcache;

import com.nsm.common.conf.YamlConfigUtils;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/31
 */
public class MemcachedUtil {
    private static final Logger logger = LoggerFactory.getLogger(MemcachedUtil.class);

    public static MemcachedClient getMemClient(){
        return ClientHolder.client;
    }

    public static MemcachedConfig getConfig(){
        return ClientHolder.config;
    }

    private static class ClientHolder{
        static MemcachedConfig config = YamlConfigUtils.loadConfig("memcache.yaml", MemcachedConfig.class);
        static MemcachedClient client= initClient();

        private static MemcachedClient initClient(){
            List<String> servers = config.servers;
            if(servers != null && !servers.isEmpty()) {
                Map<InetSocketAddress, InetSocketAddress> addressMap = new HashMap<>(servers.size());
                for (String server : servers) {
                    List<InetSocketAddress> masterSlave = AddrUtil.getAddresses(server);
                    if (masterSlave.size() > 1) {
                        addressMap.put(masterSlave.get(0), masterSlave.get(1));
                    } else {
                        addressMap.put(masterSlave.get(0), null);
                    }
                }
                XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addressMap);
                builder.setConnectTimeout(config.connectTimeout);
                builder.setOpTimeout(config.operatorTimeout);
                try {
                    return builder.build();
                } catch (Exception e) {
                    logger.error("get memcache client err" , e);
                }
            }
            return client;
        }
    }
}
