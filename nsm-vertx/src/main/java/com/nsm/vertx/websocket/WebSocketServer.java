package com.nsm.vertx.websocket;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by nieshuming on 2018/6/23
 */
public class WebSocketServer extends AbstractVerticle{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Map<Long,Map<String, ConnClient>> userClientMap = new ConcurrentHashMap<>();
    private HttpServer httpServer = null;

    @Override
    public void start() throws Exception{
        httpServer = vertx.createHttpServer();
        httpServer.websocketHandler(ws -> {
            logger.info("new socket from {}", ws.remoteAddress().host());
            ConnClient connClient = new ConnClient(ws, userClientMap);
            ws.closeHandler((v) -> connClient.onClose());
            ws.exceptionHandler(connClient::onException);
            ws.frameHandler(connClient::onFrame);

        }).listen(81);
        logger.info("server start at port {}", httpServer.actualPort());
    }

    @Override
    public void stop() throws Exception{
        if(httpServer != null){
            httpServer.close(res -> {
                System.out.println("server close " + res.succeeded());
                httpServer = null;
            });
        }
    }

    public static void main(String[] args){
        VertxOptions vOptions = new VertxOptions();
        Vertx.clusteredVertx(vOptions, res -> {
            Vertx vertx = res.result();
            DeploymentOptions dOptions = new DeploymentOptions();
            vertx.deployVerticle(WebSocketServer.class, dOptions);
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run(){
                    vertx.close();
                }
            });
        });
    }
}
