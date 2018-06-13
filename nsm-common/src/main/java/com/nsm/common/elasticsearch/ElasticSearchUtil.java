package com.nsm.common.elasticsearch;

import com.nsm.common.conf.YamlConfigUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by nieshuming on 2018/6/13
 */
public class ElasticSearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchUtil.class);

    public static TransportClient getClient(){
        return ClientHolder.client;
    }

    private static class ClientHolder{
        static ElasticSearchConfig config = YamlConfigUtils.loadConfig("elasticsearch.yaml", ElasticSearchConfig.class);
        static TransportClient client = buildClient();
        private static TransportClient buildClient(){
            Settings settings = Settings.builder()
                    .put("cluster.name", config.clusterName)
                    .put("client.transport.sniff", true)
                    .build();
            TransportClient client = new PreBuiltTransportClient(settings);
            for(String node : config.clusterNodes) {
                String[] hostPort = node.split(":");
                try {
                    TransportAddress address = new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.valueOf(hostPort[1]));
                    client.addTransportAddress(address);
                } catch (UnknownHostException e) {
                    logger.error("unknown host for node {}", node, e);
                }
            }
            return client;
        }
    }
}
