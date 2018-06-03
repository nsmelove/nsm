package com.nsm.common.fastdfs;

import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nsm on 2018/6/3
 */
public class FastdfsClient extends StorageClient1 {
    private static final Logger logger = LoggerFactory.getLogger(FastdfsClient.class);

     protected FastdfsClient(TrackerServer trackerServer, StorageServer storageServer) {
        super(trackerServer, storageServer);
    }

    /**
     * relase trackerServer and storageServer
     */
    public void close(){
        if(trackerServer != null) {
            try {
                trackerServer.close();
            } catch (IOException e){
                logger.error("close trackerServer error");
            }finally {
                trackerServer = null;
            }

        }
        if (storageServer != null) {
            try{
                storageServer.close();
            } catch (IOException e){
                logger.error("close storageServer error");
            }finally {
                storageServer = null;
            }
        }
    }
}
