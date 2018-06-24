package com.nsm.vertx.websocket;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/24.
 */
public class ConnClient{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private long userId;
    private String sessionId;
    private ServerWebSocket socket;
    private Map<Long,Map<String, ConnClient>> userClientMap;

    public ConnClient(ServerWebSocket socket, Map<Long,Map<String, ConnClient>> userClientMap){
        this.socket = socket;
        this.userClientMap = userClientMap;
    }

    public void onFrame(WebSocketFrame frame){
        String msg = frame.toString();

    }

    public void onClose(){
        logger.info("closed socket, remote {}", socket.remoteAddress());
        userClientMap.remove(userId);
    }

    public void onException(Throwable e){
        logger.error("socket cause exception {}", e);
    }
}
