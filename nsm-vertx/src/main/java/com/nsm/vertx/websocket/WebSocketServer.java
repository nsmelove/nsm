package com.nsm.vertx.websocket;

import io.vertx.core.*;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by nieshuming on 2018/6/23
 */
public class WebSocketServer extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Map<ServerWebSocket, Long> webSocketLongMap = new ConcurrentHashMap<>();
    private HttpServer httpServer = null;
    @Override
    public void start() throws Exception {
        httpServer = vertx.createHttpServer();
        httpServer.websocketHandler(ws ->{
           logger.info("new socket from {}", ws.remoteAddress().host());
            webSocketLongMap.put(ws,0L);
            ws.closeHandler((v) ->{
                logger.info("closed socket from {}", ws.remoteAddress().host());
            });
            ws.exceptionHandler((e) ->{
                logger.error("socket cause exception {}", e);
            });
            ws.frameHandler(frame ->{
                logger.error("new msg {}", frame.textData());
            });
        });
        httpServer.listen();
        logger.info("server start at port {}", httpServer.actualPort());
    }

    @Override
    public void stop() throws Exception {
        if(httpServer != null) {
            httpServer.close(res ->{
                System.out.println("server close " + res.succeeded());
                httpServer = null;
            });
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(Runtime.getRuntime().availableProcessors());
        vertx.deployVerticle(WebSocketServer.class, options, res ->{
            System.out.println("deployed verticle: " + res.result());
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
               vertx.close();
            }
        });
    }
}
