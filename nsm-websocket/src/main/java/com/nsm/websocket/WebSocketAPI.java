package com.nsm.websocket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.bean.packet.Packet;
import com.nsm.common.utils.JsonUtils;
import com.nsm.websocket.bean.Receiver;
import io.vertx.core.*;
import io.vertx.core.eventbus.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by nieshuming on 2018/6/26
 */
public class WebSocketAPI{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String addressDeployPrefix ="ws.deploy.";
    private final static String headerReceiver ="receivers";
    private final static String sharedDataDeploy = "deployMap";
    private final static String sdDpUsSePrefix = "uSessionMap.";
    private EventBus eventBus;
    private SharedData sharedData;
    private MessageConsumer<String> consumer;
    private WebSocketAPI(){}
    public static WebSocketAPI create(Vertx vertx){
        WebSocketAPI client = new WebSocketAPI();
        client.eventBus = vertx.eventBus();
        client.sharedData = vertx.sharedData();
        return client;
    };

    public WebSocketAPI getDeployMap(Handler<AsyncResult<AsyncMap<String, Integer>>> resultHandler){
        sharedData.getAsyncMap(sharedDataDeploy, resultHandler::handle);
        return this;
    }

    public WebSocketAPI getDeployIdSet(Handler<Set<String>> resultHandler){
        getDeployMap(res -> {
            if (res.succeeded()) {
                AsyncMap<String, Integer> deployMap= res.result();
                deployMap.keys(keysRes -> {
                    if(keysRes.succeeded()) {
                        Set<String> keySet = keysRes.result();
                        resultHandler.handle(keySet != null ? keySet : Collections.emptySet());
                    }else {
                        //TODO logger it
                        resultHandler.handle(Collections.emptySet());
                    }
                });
            }else {
                //TODO logger it
                resultHandler.handle(Collections.emptySet());
            }
        });
        return this;
    }

    public WebSocketAPI getUsersSessionMap(String deployId, Handler<AsyncResult<AsyncMap<Long,Set<String>>>> resultHandler){
        String shareDataName = sdDpUsSePrefix + deployId;
        sharedData.getAsyncMap(shareDataName, resultHandler::handle);
        return this;
    }

    public Future<AsyncMap<Long,Set<String>>> getUsersSessionMap(String deployId){
        Future<AsyncMap<Long,Set<String>>> future = Future.future();
        getUsersSessionMap(deployId, res ->{
            if(res.succeeded()) {
                future.complete(res.result());
            }else {
                future.fail(res.cause());
            }
        });
        return future;
    }

    public WebSocketAPI getUserSessionSet(String deployId, long userId, Handler<Set<String>> resultHandler){
        getUsersSessionMap(deployId, res -> {
            if(res.succeeded()) {
                res.result().get(userId, uSessionSetRes -> {
                    Set<String> uSessionSet = uSessionSetRes.result();
                    resultHandler.handle(uSessionSet != null ? uSessionSet : Collections.emptySet());
                });
            }else {
                //TODO logger it
                resultHandler.handle(Collections.emptySet());
            }
        });
        return this;
    }

    public Future<Set<String>> getUserSessionSet(String deployId, long userId){
        Future<Set<String>> future = Future.future();
        getUserSessionSet(deployId, userId, future::complete);
        return future;
    }

    public WebSocketAPI consume(String deployId, BiFunction<Map<Long, Set<String>>, Packet, Map<Long, Set<String>>> packetHandler){
        String address = addressDeployPrefix + deployId;
        if(this.consumer == null || !this.consumer.isRegistered()) {
            this.consumer = eventBus.consumer(address);
        }else if(!this.consumer.address().equals(address)){
            unConsume(deployId);
            this.consumer = eventBus.consumer(address);
        }
        getDeployMap(getRes ->{
            if (getRes.succeeded()) {
                getRes.result().put(deployId, 0, putRes -> {
                    if (putRes.succeeded()) {
                        logger.info("deployId:{} join event bus success", deployId);
                    } else {
                        logger.error("deployId:{} join event bus failed", deployId, putRes.cause());
                    }
                });
            } else {
                logger.error("get deployMap shareData failed", getRes.cause());
            }
        });

        //消费消息实现
        consumer.handler(msg ->{
            logger.info("consumer:{} received msg, headers: {}, body: {}", consumer.address(), msg.headers(), msg.body());
            MultiMap heads = msg.headers();
            String receiverStr = heads.get(headerReceiver);
            Packet packet = JsonUtils.toObject(msg.body(), Packet.class);
            Map<Long, Set<String>> userSessionMap = new HashMap<>(1);
            JsonObject jsonObject = new JsonObject(receiverStr);
            for(String uid : jsonObject.fieldNames()) {
                userSessionMap.put(Long.valueOf(uid), Sets.newHashSet(jsonObject.getJsonArray(uid).getList()));
            }
            Map<Long, Set<String>> successUserSessionMap = packetHandler.apply(userSessionMap, packet);
            msg.reply(JsonUtils.toJson(successUserSessionMap));
        });
        return this;
    }

    public WebSocketAPI unConsume(String deployId){
        String address = addressDeployPrefix + deployId;
        if(this.consumer != null && this.consumer.isRegistered() && consumer.address().equals(address)) {
            this.consumer.unregister();
            this.consumer = null;
            getDeployMap(getRes ->{
                if(getRes.succeeded()) {
                    getRes.result().remove(deployId, remRes -> {
                        if (remRes.succeeded()) {
                            logger.info("deployId:{} quit event bus success", deployId);
                        } else {
                            logger.error("deployId:{} quit event bus failed", deployId, remRes.cause());
                        }
                    });
                }else {
                    logger.error("get deployMap shareData failed", getRes.cause());
                }
            });
        }
        return this;
    }

    public WebSocketAPI join(long userId, String sessionId, String deployId){
        getUsersSessionMap(deployId, getRes -> {
            if (getRes.succeeded()) {
                AsyncMap<Long, Set<String>> usersSessionMap = getRes.result();
                usersSessionMap.get(userId, sessionRes -> {
                    if (sessionRes.succeeded()) {
                        Set<String> userSessionSet = sessionRes.result();
                        if (userSessionSet == null) {
                            userSessionSet = Sets.newHashSetWithExpectedSize(1);
                        }
                        userSessionSet.add(sessionId);
                        usersSessionMap.put(userId, userSessionSet, putRes -> {
                            if (putRes.succeeded()) {
                                logger.error("user:{} session:{} join deployId:{} success", userId, sessionId, deployId);
                            } else {
                                logger.error("user:{} session:{} join deployId:{} failed", userId, sessionId, deployId, putRes.cause());
                            }
                        });
                    } else {
                        logger.error("get user:{} sessionSet failed", userId, sessionRes.cause());
                    }
                });
            } else {
                logger.error("getUsersSessionMap:{} failed", deployId, getRes.cause());
            }
        });
        return this;
    }

    public WebSocketAPI quit(long userId, String sessionId, String deployId){
        getUsersSessionMap(deployId, getRes -> {
            if (getRes.succeeded()) {
                AsyncMap<Long, Set<String>> usersSessionMap = getRes.result();
                usersSessionMap.get(userId, sessionRes -> {
                    if (sessionRes.succeeded()) {
                        Set<String> userSessionSet = sessionRes.result();
                        if (userSessionSet != null) {
                            userSessionSet.remove(sessionId);
                            if (userSessionSet.isEmpty()) {
                                usersSessionMap.remove(userId, remRes -> {
                                    if (remRes.succeeded()) {
                                        logger.info("user:{} session:{} quit deployId:{} success", userId, sessionId, deployId);
                                    } else {
                                        logger.error("user:{} session:{} quit deployId:{} failed", userId, sessionId, deployId, remRes.cause());
                                    }
                                });
                            } else {
                                usersSessionMap.put(userId, userSessionSet, putRes -> {
                                    if (putRes.succeeded()) {
                                        logger.info("user:{} session:{} quit deployId:{} success", userId, sessionId, deployId);
                                    } else {
                                        logger.error("user:{} session:{} quit deployId:{} failed", userId, sessionId, deployId, putRes.cause());
                                    }
                                });
                            }
                        }
                    } else {
                        logger.error("get user:{} sessionSet failed", userId, sessionRes.cause());
                    }
                });
            } else {
                logger.error("getUsersSessionMap:{} failed", deployId, getRes.cause());
            }
        });
        return this;
    }

//    public WebSocketAPI quit(long userId, Collection<String> sessionIds){
//        if(userSessionMap == null || CollectionUtils.isEmpty(sessionIds)) {
//            return this;
//        }
//        userSessionMap.get(userId, sidRes ->{
//            if(sidRes.succeeded()) {
//                Map<String,String> sidDeployIdMap = sidRes.result();
//                if(sidDeployIdMap != null) {
//                    sessionIds.forEach(sessionId ->{
//                        sidDeployIdMap.remove(sessionId);
//                    });
//                    if(sidDeployIdMap.isEmpty()) {
//                        userSessionMap.remove(userId, remRes ->{
//                            logger.debug("user:{} sessions:{} quit success ? {}", userId, sessionIds, remRes.succeeded());
//                        });
//                    }else {
//                        userSessionMap.put(userId, sidDeployIdMap, putRes ->{
//                            logger.debug("user:{} sessions:{} quit success ? {}", userId, sessionIds, putRes.succeeded());
//                        });
//                    }
//                }
//            }else {
//                logger.error("user:{} sessions:{} quit deployId failed", userId, sessionIds, sidRes.cause());
//            }
//        });
//        return this;
//    }
//
//    public WebSocketAPI joinSessions(long userId, Handler<Map<String,String>> resultHandler){
//        if(userSessionMap == null) {
//            resultHandler.handle(null);
//            return this;
//        }
//        userSessionMap.get(userId, sidRes ->{
//            resultHandler.handle(sidRes.result());
//        });
//        return this;
//    }

    private Future<Map<Long, Set<String>>> sendPacket(String deployId, Map<Long, Set<String>> uSidsMap, Packet packet) {
        Future<Map<Long, Set<String>>> future = Future.future();
        String address = addressDeployPrefix + deployId;
        String packetStr = JsonUtils.toJson(packet);
        String receiverStr = JsonUtils.toJson(uSidsMap);
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader(headerReceiver, receiverStr);
        options.setSendTimeout(1000);
        logger.debug("event bus send deploy:{} packet:{},receivers:{}", deployId, packet, receiverStr);
        eventBus.<String>send(address, packetStr, options, sendRes -> {
            Map<Long, Set<String>> successUSidsMap = null;
            if (sendRes.succeeded()) {
                String successReceiverStr = sendRes.result().body();
                Map<Long, Set<String>> failedUSidsMap = Maps.newHashMapWithExpectedSize(uSidsMap.size());
                if(successReceiverStr != null) {
                    successUSidsMap = Maps.newHashMapWithExpectedSize(uSidsMap.size());
                    JsonObject jsonObject = new JsonObject(successReceiverStr);
                    for(String uid: jsonObject.fieldNames()) {
                        successUSidsMap.put(Long.valueOf(uid), Sets.newHashSet(jsonObject.getJsonArray(uid).getList()));
                    }
                    successUSidsMap.forEach((uid, sidSet) ->{
                        Set<String> sendSidSet = uSidsMap.get(uid);
                        if(sendSidSet != null) {
                            Set<String> failedSet = Sets.newHashSet(sendSidSet);
                            failedSet.removeAll(sidSet);
                            failedUSidsMap.put(uid, failedSet);
                        }
                    });
                }else {
                    failedUSidsMap.putAll(uSidsMap);
                }
                if (!failedUSidsMap.isEmpty()) {
                    //移除无效的用户session
                    getUsersSessionMap(deployId, res ->{
                        if(res.succeeded()) {
                            AsyncMap<Long,Set<String>> usersSessionMap = res.result();
                            failedUSidsMap.forEach((uid, sidSet) -> {
                                usersSessionMap.get(uid, userSessionSetRes ->{
                                    if(userSessionSetRes.succeeded()) {
                                        Set<String> userSessionSet = userSessionSetRes.result();
                                        if (userSessionSet != null) {
                                            userSessionSet.removeAll(sidSet);
                                            if (userSessionSet.isEmpty()) {
                                                usersSessionMap.remove(uid, remRes -> {});
                                            } else {
                                                usersSessionMap.put(uid, userSessionSet, putRes -> {});
                                            }
                                        }
                                    }
                                });
                            });
                        }
                    });
                }
            } else {
                ReplyException replyException = (ReplyException) sendRes.cause();
                if (replyException.failureType().equals(ReplyFailure.NO_HANDLERS)) {
                    logger.warn("consumer:{} not found , remove it", deployId);
                    getDeployMap(deployMapRes -> {
                        if (deployMapRes.succeeded()) {
                            deployMapRes.result().remove(deployId, res -> {});
                        }
                    });
                    getUsersSessionMap(deployId, usersSessionMapRes ->{
                        if(usersSessionMapRes.succeeded()) {
                            usersSessionMapRes.result().clear(res ->{});
                        }
                    });
                }
            }
            future.complete(successUSidsMap);
        });
        return future;
    }

    public WebSocketAPI sendPacket(Collection<Receiver> receivers, Packet packet, Handler<Map<Long, Set<String>>> successHandler){
        logger.debug("event bus send packet:{},receivers:{}", packet, receivers);
        if(CollectionUtils.isEmpty(receivers) || packet == null) {
            successHandler.handle(Collections.emptyMap());
            return this;
        }
        getDeployIdSet(deployIdSet -> {
            List<Future> allSuccessFutures  = Lists.newArrayListWithExpectedSize(deployIdSet.size());
            deployIdSet.forEach(deployId -> {
                Future<Map<Long, Set<String>>> successFuture = Future.future();
                allSuccessFutures.add(successFuture);
                Map<Long, Future<Set<String>>> getSessionFutureMap = Maps.newHashMapWithExpectedSize(receivers.size());
                receivers.forEach(receiver -> {
                    getSessionFutureMap.put(receiver.getUserId(), getUserSessionSet(deployId, receiver.getUserId()));
                });
                CompositeFuture.all(Lists.newArrayList(getSessionFutureMap.values())).setHandler(compFutureRes -> {
                    if(compFutureRes.succeeded()) {
                        Map<Long, Set<String>> uSidSetMap = Maps.newHashMapWithExpectedSize(receivers.size());
                        receivers.forEach(receiver -> {
                            Set<String> uSidSet = getSessionFutureMap.get(receiver.getUserId()).result();
                            if(uSidSet != null && !uSidSet.isEmpty()){
                                if (receiver.getSessionIds() != null) {
                                    uSidSet.retainAll(receiver.getSessionIds());
                                }
                                if (receiver.getIgnoreSids() != null) {
                                    uSidSet.removeAll(receiver.getIgnoreSids());
                                }
                                if(!uSidSet.isEmpty()) {
                                    uSidSetMap.put(receiver.getUserId(), uSidSet);
                                }
                            }
                        });
                        if(!uSidSetMap.isEmpty()){
                            Future<Map<Long, Set<String>>> sendFuture = sendPacket(deployId, uSidSetMap, packet);
                            sendFuture.setHandler(res -> successFuture.complete(res.result()));
                        }else {
                            successFuture.complete(uSidSetMap);
                        }
                    }
                });
            });
            if (successHandler != null) {
                //所有发送消息完成
                CompositeFuture.all(allSuccessFutures).setHandler(comFutureRes-> {
                    if(comFutureRes.succeeded()) {
                        List<Map<Long, Set<String>>> allSendUSid = comFutureRes.result().list();
                        Map<Long, Set<String>> allSendMap = Maps.newHashMapWithExpectedSize(receivers.size());
                        allSendUSid.forEach(uSidMap ->{
                            if(uSidMap != null) {
                                uSidMap.forEach((uid, uSidSet) ->{
                                    if(allSendMap.containsKey(uid)){
                                        allSendMap.get(uid).addAll(uSidSet);
                                    }else {
                                        allSendMap.put(uid, uSidSet);
                                    }
                                });
                            }
                        });
                        successHandler.handle(allSendMap);
                    }else {
                        successHandler.handle(Collections.emptyMap());
                    }
                });
            }
        });
        return this;
    }
}
