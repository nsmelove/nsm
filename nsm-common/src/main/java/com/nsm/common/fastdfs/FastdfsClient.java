package com.nsm.common.fastdfs;

import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nsm on 2018/6/3
 */
public class FastdfsClient extends StorageClient1 {
    private static final Logger logger = LoggerFactory.getLogger(FastdfsClient.class);

    private FastdfsClient(TrackerServer trackerServer, StorageServer storageServer) {
        super(trackerServer, storageServer);
    }

    public static FastdfsClient getClient() {
        return FastdfsBuilder.getClient();
    }

    public static StorageClient1 getStorageClient1() {
        return FastdfsBuilder.getStorageClient1();
    }

    /**
     * relase trackerServer and storageServer
     */
    public void close() {
        if (trackerServer != null) {
            try {
                trackerServer.close();
            } catch (IOException e) {
                logger.error("close trackerServer error");
            } finally {
                trackerServer = null;
            }

        }
        if (storageServer != null) {
            try {
                storageServer.close();
            } catch (IOException e) {
                logger.error("close storageServer error");
            } finally {
                storageServer = null;
            }
        }
    }

    private static class FastdfsBuilder {
        static {
            try {
                String confFile = "fastdfs-client.properties";
                ClientGlobal.initByProperties(confFile);
            } catch (Exception e) {
                logger.error("init fastdfs config error", e);

            }
        }

        public static FastdfsClient getClient() {
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

        public static StorageClient1 getStorageClient1() {
            return new StorageClient1();
        }
    }
}
