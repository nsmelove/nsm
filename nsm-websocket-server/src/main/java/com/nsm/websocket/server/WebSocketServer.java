package com.nsm.websocket.server;
import com.nsm.core.config.SystemConfig;
import com.nsm.websocket.server.service.WebSocketService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * Created by nieshuming on 2018/6/23
 */
public class WebSocketServer extends AbstractVerticle{
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private HttpServer httpServer = null;
    private WebSocketService webSocketService = null;

    @Override
    public void start() throws Exception{
        vertx.<WebSocketService>executeBlocking(f ->{
            webSocketService = WebSocketService.create(this);
            f.complete(webSocketService);
        }, res ->{
            if(res.succeeded()) {
                httpServer = vertx.createHttpServer();
                httpServer.websocketHandler(ws -> {
                    logger.info("new socket from: {}", ws.remoteAddress());
                    new ConnClient(res.result(), ws);
                }).listen(81, serverRes ->{
                    if(serverRes.succeeded()){
                        logger.info("server start at port {}", serverRes.result().actualPort());
                    }else {
                        logger.error("server start failed", serverRes.cause());
                    }
                });

            }else {
                logger.error("server start failed", res.cause());
            }

        });
    }

    @Override
    public void stop() throws Exception{
        if(httpServer != null){
            httpServer.close(res -> {
                logger.info("server close {}", res.succeeded());
                httpServer = null;
            });
        }
    }

    public static void main(String[] args){
        Consumer<Vertx> run = (vertx) ->{
            DeploymentOptions dOptions = new DeploymentOptions();
            vertx.deployVerticle(WebSocketServer.class, dOptions, res ->{
                if(res.succeeded()) {
                    logger.info("deploy verticle webSocketServer:{} success", res.result());
                }else {
                    logger.error("deploy verticle webSocketServer failed", res.cause());
                }
            });
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run(){
                    vertx.close();
                }
            });
        };
        VertxOptions vOptions = new VertxOptions();
        if(SystemConfig.clusterMode) {
            Vertx.clusteredVertx(vOptions, res -> {
                if(res.succeeded()) {
                    run.accept(res.result());
                }else {
                    logger.error("create cluster vertx failed", res.cause());
                }
            });
        }else {
            run.accept(Vertx.vertx());
        }
    }
}
