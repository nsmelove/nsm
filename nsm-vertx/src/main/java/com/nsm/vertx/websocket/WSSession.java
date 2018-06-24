package com.nsm.vertx.websocket;

import com.nsm.core.bean.Session;
import io.vertx.core.http.ServerWebSocket;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/24.
 */
public class WSSession extends Session{

    private ServerWebSocket webSocket;

    public ServerWebSocket getWebSocket(){
        return webSocket;
    }

    public void setWebSocket(ServerWebSocket webSocket){
        this.webSocket = webSocket;
    }
}
