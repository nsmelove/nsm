package com.nsm.common.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nsm on 2018/6/3
 */
public class FastdfsUtil {
    private static final Logger logger = LoggerFactory.getLogger(FastdfsUtil.class);
    private static volatile boolean init = false;
    public static FastdfsClient getClient(){
        if (!init){
            init = true;
            try {
                String confFile = "fastdfs-client.properties";
                ClientGlobal.initByProperties(confFile);
            } catch (Exception e) {
                logger.error("init fastdfs config error", e);
                return null;
            }
        }
        try {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = tracker.getStoreStorage(trackerServer);
            trackerServer = tracker.getConnection();
            return new FastdfsClient(trackerServer, storageServer);
        } catch (IOException e) {
            logger.error("get fastdfs trackerServer error", e);
            return null;
        }


    }
}