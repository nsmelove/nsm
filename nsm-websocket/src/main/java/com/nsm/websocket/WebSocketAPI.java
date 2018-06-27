package com.nsm.websocket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.bean.packet.Packet;
import com.nsm.common.utils.JsonUtils;
import com.nsm.websocket.bean.Receiver;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by nieshuming on 2018/6/26
 */
public class WebSocketAPI{
    private static Vertx vertx ;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String addressPrefix ="ws.packet.";
    private final static String headerReceiver ="receiver";
    private final static String sharedDataUserSession = "userSessionMap";
    private EventBus eventBus;
    private SharedData sharedData;
    private AsyncMap<Long,Map<String,String>> userSessionMap;
    private MessageConsumer<String> consumer;
    private WebSocketAPI(){}
    public static WebSocketAPI create(Vertx vertx){
        if (WebSocketAPI.vertx == null) {
            WebSocketAPI.vertx = vertx;
        }
        WebSocketAPI client = new WebSocketAPI();
        client.eventBus = vertx.eventBus();
        client.sharedData = vertx.sharedData();
        client.sharedData.<Long,Map<String,String>>getAsyncMap(sharedDataUserSession, res ->{
            client.userSessionMap = res.result();
        });
        return client;
    };

    public WebSocketAPI consume(String deployId, BiConsumer<List<Receiver>, Packet> packetHandler){
        String address = addressPrefix + deployId;
        if(this.consumer == null || !this.consumer.isRegistered()) {
            this.consumer = eventBus.consumer(address);
        }else if(!this.consumer.address().equals(address)){
            this.consumer.unregister();
            this.consumer = eventBus.consumer(address);
        }
        consumer.handler(msg ->{
            logger.info("consumer {} received msg, headers: {}, body: {}", consumer.address(), msg.headers(), msg.body());
            String packetStr = msg.body();
            MultiMap heads = msg.headers();
            String receiverStr = heads.get(headerReceiver);
            List<Receiver> receivers = JsonUtils.toList(receiverStr, Receiver.class);
            Packet packet = Packet.parseFrom(packetStr);;
            packetHandler.accept(receivers, packet);
        });
        return this;
    }

    public WebSocketAPI join(long userId, String sessionId, String deployId){
        if(userSessionMap == null) {
            logger.warn("join ignored, because userSessionMap has not initialized yet !");
            return this;
        }
        userSessionMap.get(userId, sidRes ->{
            if(sidRes.succeeded()) {
                Map<String,String> sidDeployIdMap = sidRes.result();
                if(sidDeployIdMap == null) {
                    sidDeployIdMap = Maps.newHashMapWithExpectedSize(1);
                }
                sidDeployIdMap.put(sessionId, deployId);
                userSessionMap.put(userId, sidDeployIdMap, putRes ->{
                    logger.debug("user:{} session:{} join deployId:{} success ?  {}", userId, sessionId, deployId, putRes.succeeded());
                });
            }else {
                logger.error("user:{} session:{} join deployId:{} failed", userId, sessionId, deployId, sidRes.cause());
            }
        });
        return this;
    }

    public WebSocketAPI quit(long userId, String sessionId){
        if(userSessionMap == null) {
            return this;
        }
        userSessionMap.get(userId, sidRes ->{
            if(sidRes.succeeded()) {
                Map<String,String> sidDeployIdMap = sidRes.result();
                if(sidDeployIdMap != null) {
                    String deployId = sidDeployIdMap.remove(sessionId);
                    if(sidDeployIdMap.isEmpty()) {
                        userSessionMap.remove(userId, remRes ->{
                            logger.debug("user:{} session:{} quit deployId:{} success ? {}", userId, sessionId, deployId, remRes.succeeded());
                        });
                    }else {
                        userSessionMap.put(userId, sidDeployIdMap, putRes ->{
                            logger.debug("user:{} session:{} quit deployId:{} success ? {}", userId, sessionId, deployId, putRes.succeeded());
                        });
                    }
                }
            }else {
                logger.error("user:{} session:{} quit deployId failed", userId, sessionId, sidRes.cause());
            }
        });
        return this;
    }

    public WebSocketAPI joinSessions(long userId, Handler<Map<String,String>> resultHandler){
        if(userSessionMap == null) {
            resultHandler.handle(null);
            return this;
        }
        userSessionMap.get(userId, sidRes ->{
            resultHandler.handle(sidRes.result());
        });
        return this;
    }

    public WebSocketAPI sendPacket(Collection<Receiver> receivers, Packet packet){
        if(userSessionMap == null) {
            logger.warn("send ignored, because userSessionMap has not initialized yet !");
            return this;
        }
        logger.info("event bus send packet:{},receivers:{}", packet, receivers);
        Map<String,List<Receiver>> deployIdReceiverMap = Maps.newConcurrentMap();
        List<Future> futures = Lists.newArrayList();
        for(Receiver receiver : receivers) {
            Future future = Future.<Map<String,String>>future();
            userSessionMap.get(receiver.getUserId(), res ->{
                Map<String,String> sidDeployIdMap = res.result();
                System.out.println("sidDeployIdMap:" + sidDeployIdMap);
                if(sidDeployIdMap != null) {
                    Map<String,Set<String>> deploySidsMap = Maps.newHashMap();
                    if(CollectionUtils.isNotEmpty(receiver.getSessionIds())) {
                        for(String sid : receiver.getSessionIds()) {
                            String deployId = sidDeployIdMap.get(sid);
                            if(deployId != null) {
                                Set<String> sidSet = deploySidsMap.computeIfAbsent(deployId, Sets::newHashSet);
                                sidSet.add(sid);
                            }
                        }
                    }else {
                        sidDeployIdMap.forEach((sid,deployId) ->{
                            Set<String> sidSet = deploySidsMap.computeIfAbsent(deployId, Sets::newHashSet);
                            sidSet.add(sid);
                        });
                    }
                    deploySidsMap.forEach((deployId, sids) -> {
                        if (receiver.getIgnoreSids() != null) {
                            sids.removeAll(receiver.getIgnoreSids());
                        }
                        Receiver receiverNew = Receiver.newReceiver(receiver.getUserId(), sids);
                        List<Receiver> deployIdReceives = deployIdReceiverMap.computeIfAbsent(deployId, d -> Lists.newArrayList());
                        deployIdReceives.add(receiverNew);
                    });
                }
                future.complete(null);
            });
            futures.add(future);
        }
        CompositeFuture.all(futures).setHandler(allRes ->{
            String msg = JsonUtils.toJson(packet);
            deployIdReceiverMap.forEach((deployId, recvs) ->{
                String receiverStr = JsonUtils.toJson(recvs);
                DeliveryOptions options = new DeliveryOptions();
                options.addHeader(headerReceiver,receiverStr);
                eventBus.send(addressPrefix + deployId, msg, options);
            });
        });
        return this;
    }
}
