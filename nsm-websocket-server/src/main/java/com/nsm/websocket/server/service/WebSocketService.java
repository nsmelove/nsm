package com.nsm.websocket.server.service;

import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.bean.ErrorCode;
import com.nsm.bean.message.Message;
import com.nsm.bean.message.RegInfo;
import com.nsm.bean.packet.Packet;
import com.nsm.core.SpringContainer;
import com.nsm.core.service.MessageService;
import com.nsm.core.service.SessionService;
import com.nsm.core.service.UserGroupService;
import com.nsm.websocket.eventbus.EventBusClient;
import com.nsm.websocket.eventbus.Receiver;
import com.nsm.websocket.server.ConnClient;

import com.nsm.websocket.server.WebSocketServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nieshuming on 2018/6/25
 */
public interface WebSocketService {

    void sendPacket(long userId, Packet packet);

    void sendPacket(long userId, Packet packet, Collection<String> ignoreSids);

    void sendPacket(Collection<Receiver> receivers, Packet packet);
    /**
     * 注册请求
     *
     * @param client
     * @param regInfo
     * @param resultHandler
     */
    void register(ConnClient client, RegInfo regInfo, Handler<ErrorCode> resultHandler);

    void unRegister(ConnClient client, Handler<ErrorCode> resultHandler);

    void sendMessage(ConnClient connClient, Message message, Handler<ErrorCode> resultHandler);

    static WebSocketService create(AbstractVerticle verticle) {
        return new WebSocketService() {
            private Logger logger = LoggerFactory.getLogger(this.getClass());
            private String deploymentID =verticle.deploymentID();
            private Vertx vertx = verticle.getVertx();
            private Map<Long,Map<String, ConnClient>> userClientMap = new ConcurrentHashMap<>();
            private EventBusClient eventBusClient = EventBusClient.create(vertx).consume(deploymentID, this::sendPacketLocal);
            private SessionService sessionService = SpringContainer.getBean(SessionService.class);
            private MessageService messageService = SpringContainer.getBean(MessageService.class);
            private UserGroupService userGroupService = SpringContainer.getBean(UserGroupService.class);

            private void addToRegisterClient(ConnClient client){
                Map<String, ConnClient> sessionClientMap = userClientMap.computeIfAbsent(client.getUserId(), (u) -> Maps.newConcurrentMap());
                sessionClientMap.put(client.getSessionId(), client);
                eventBusClient.join(deploymentID, client.getUserId(), client.getSessionId());
            }

            private void removeFromRegisterClient(ConnClient client){
                Map<String, ConnClient> sessionClientMap = userClientMap.get(client.getUserId());
                if(sessionClientMap != null) {
                    sessionClientMap.remove(client.getSessionId());
                }
                eventBusClient.quit(client.getUserId(), client.getSessionId());
            }

            private void sendPacketLocal(Collection<Receiver> receivers, Packet packet){
                logger.info("send local packet:{} ,receivers:{}", packet, receivers);
                receivers.forEach(receiver ->{
                    Map<String, ConnClient> sessionClientMap = userClientMap.get(receiver.getUserId());
                    if(sessionClientMap != null) {
                        sessionClientMap.forEach((sid, client) -> {
                            if ((CollectionUtils.isEmpty(receiver.getSessionIds()) || receiver.getSessionIds().contains(sid))
                                    && (receiver.getIgnoreSids() == null || !receiver.getIgnoreSids().contains(sid))) {
                                client.sendPacket(packet);
                            }
                        });
                    }
                });
            }

            @Override
            public void sendPacket(long userId, Packet packet){
                logger.info("send packet:{},userId:{}", packet, userId);
                Map<String, ConnClient> sessionClientMap = userClientMap.get(userId);
                Set<String> busIgnoreSids = Sets.newHashSet();
                if(sessionClientMap != null) {
                    sessionClientMap.values().forEach(client ->{
                        client.sendPacket(packet);
                    });
                    busIgnoreSids.addAll(sessionClientMap.keySet());
                }
                Receiver otherReceiver = Receiver.newReceiverIgnore(userId, busIgnoreSids);
                eventBusClient.sendPacket(Lists.newArrayList(otherReceiver), packet);
            }

            @Override
            public void sendPacket(long userId, Packet packet, Collection<String> ignoreSids){
                logger.info("send packet:{},userId:{},ignoreSids:{}", packet, userId, ignoreSids);
                Map<String, ConnClient> sessionClientMap = userClientMap.get(userId);
                Set<String> busIgnoreSids = Sets.newHashSet();
                if(sessionClientMap != null){
                    sessionClientMap.forEach((sid, client) -> {
                        if (ignoreSids == null || !ignoreSids.contains(sid)) {
                            client.sendPacket(packet);
                        }
                        busIgnoreSids.add(sid);
                    });
                }
                if(CollectionUtils.isNotEmpty(ignoreSids)) {
                    busIgnoreSids.addAll(ignoreSids);
                }
                Receiver otherReceiver = Receiver.newReceiverIgnore(userId, busIgnoreSids);
                eventBusClient.sendPacket(Lists.newArrayList(otherReceiver), packet);
            }

            @Override
            public void sendPacket(Collection<Receiver> receivers, Packet packet){
                logger.info("send packet:{},receivers:{}", packet, receivers);
                List<Receiver> otherReceivers = Lists.newArrayListWithExpectedSize(receivers.size());
                receivers.forEach(receiver ->{
                    Map<String, ConnClient> sessionClientMap = userClientMap.get(receiver.getUserId());
                    if(sessionClientMap != null) {
                        sessionClientMap.forEach((sid, client) -> {
                            if ((CollectionUtils.isEmpty(receiver.getSessionIds()) || receiver.getSessionIds().contains(sid))
                                    && (receiver.getIgnoreSids() == null || !receiver.getIgnoreSids().contains(sid))) {
                                client.sendPacket(packet);
                            }
                        });
                    }
                    Receiver otherReceiver = Receiver.newReceiver(receiver.getUserId());
                    if(sessionClientMap != null) {
                        otherReceiver.addToIgnoreSids(sessionClientMap.keySet());
                    }
                    if (receiver.getIgnoreSids() != null) {
                        otherReceiver.addToIgnoreSids(receiver.getIgnoreSids());
                    }
                    otherReceivers.add(otherReceiver);
                });
                eventBusClient.sendPacket(otherReceivers, packet);
            }

            @Override
            public void register(ConnClient client, RegInfo regInfo, Handler<ErrorCode> resultHandler) {
                if (regInfo.getUserId() == 0 || StringUtils.isBlank(regInfo.getSessionId())) {
                    resultHandler.handle(ErrorCode.AUTHENTICATION_FAILURE);
                }
                vertx.<ErrorCode>executeBlocking(f ->{
                    long userId = sessionService.getUserId(regInfo.getSessionId());
                    if (regInfo.getUserId() != userId) {
                        f.complete(ErrorCode.AUTHENTICATION_FAILURE);
                    } else {
                        client.authenticated(userId, regInfo.getSessionId());
                        addToRegisterClient(client);
                        f.complete(ErrorCode.OK);
                    }
                }, false, res -> {
                    resultHandler.handle(res.result());
                });

            }

            @Override
            public void unRegister(ConnClient client, Handler<ErrorCode> resultHandler){
                removeFromRegisterClient(client);
                client.unAuthenticated();
                resultHandler.handle(ErrorCode.OK);
            }

            @Override
            public void sendMessage(ConnClient client, Message message, Handler<ErrorCode> resultHandler) {
                if (message.getToId() == 0) {
                    resultHandler.handle(ErrorCode.NOT_FOUND);
                }else {
                    resultHandler.handle(ErrorCode.OK);
                }
                message.setFromId(client.getUserId());
                message.setTimestamp(System.currentTimeMillis());
                Packet msgPacket = Packet.newPacket(message.getTimestamp(), Packet.DataType.MESSAGE, message);
                vertx.executeBlocking(f ->{
                    if (message.getToType() == Message.ToType.GROUP.ordinal()) {
                        List<Long> memberIds = userGroupService.groupMemberIdList(message.getToId());
                        for (Long memberId : memberIds) {
                            if (memberId == client.getUserId()) {
                                sendPacket(message.getFromId(), msgPacket, Lists.newArrayList(client.getSessionId()));
                            } else {
                                sendPacket(memberId, msgPacket);
                            }
                        }
                    } else {
                        sendPacket(message.getToId(), msgPacket);
                        sendPacket(message.getFromId(), msgPacket, Lists.newArrayList(client.getSessionId()));
                    }
                    messageService.addMsg(message);
                    f.complete(null);
                },res -> {});

            }

        };
    }

}
