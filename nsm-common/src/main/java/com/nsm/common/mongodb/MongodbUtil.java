package com.nsm.common.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.nsm.common.conf.YamlConfigUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/12.
 */
public class MongodbUtil {

    public static MongoDatabase getDataBase(){
        return getClient().getDatabase(ClientHolder.config.dbname);
    }
    public static MongoClient getClient(){
        return ClientHolder.client;
    }

    private static class ClientHolder{
        static MongodbConfig config = YamlConfigUtils.loadConfig("mongodb.yaml", MongodbConfig.class);
        static MongoClient client = getClient();

        private static MongoClient getClient(){
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            for(String server : config.servers){
                String[] hostPort = server.split(":");
                addrs.add(new ServerAddress(hostPort[0], Integer.valueOf(hostPort[1])));
            }
            //通过连接认证获取MongoDB连接
            return new MongoClient(addrs);
        }

    }
}
