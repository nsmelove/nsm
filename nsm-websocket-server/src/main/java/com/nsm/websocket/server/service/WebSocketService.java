package com.nsm.websocket.server.service;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nsm.bean.ErrorCode;
import com.nsm.bean.message.Message;
import com.nsm.bean.message.RegInfo;
import com.nsm.bean.packet.Packet;
import com.nsm.common.utils.IdUtils;
import com.nsm.core.SpringContainer;
import com.nsm.core.service.MessageService;
import com.nsm.core.service.SessionService;
import com.nsm.core.service.UserGroupService;
import com.nsm.websocket.WebSocketAPI;
import com.nsm.websocket.bean.Receiver;
import com.nsm.websocket.server.ConnClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nieshuming on 2018/6/25
 */
public interface WebSocketService {

    void sendPacket(long userId, Packet packet);

    void sendPacketIgnore(long userId, Packet packet, Collection<String> ignoreSids);

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
            private WebSocketAPI webSocketAPI = WebSocketAPI.create(vertx).consume(deploymentID, this::sendPacketLocal).subscribe(this::sendPacketLocal);
            private SessionService sessionService = SpringContainer.getBean(SessionService.class);
            private MessageService messageService = SpringContainer.getBean(MessageService.class);
            private UserGroupService userGroupService = SpringContainer.getBean(UserGroupService.class);

            private void addToRegisterClient(ConnClient client){
                Map<String, ConnClient> sessionClientMap = userClientMap.computeIfAbsent(client.getUserId(), (u) -> Maps.newConcurrentMap());
                sessionClientMap.put(client.getSessionId(), client);
                webSocketAPI.join(client.getUserId(), client.getSessionId(), deploymentID);
            }

            private void removeFromRegisterClient(ConnClient client){
                Map<String, ConnClient> sessionClientMap = userClientMap.get(client.getUserId());
                if(sessionClientMap != null) {
                    sessionClientMap.remove(client.getSessionId());
                }
                webSocketAPI.quit(client.getUserId(), client.getSessionId(), deploymentID);
            }

            public Map<Long, Set<String>> sendPacketLocal(Map<Long, Set<String>> receivers, Packet packet){
                logger.debug("send local packet:{} ,receivers:{}", packet, receivers);
                Map<Long, Set<String>> successReceivers = Maps.newHashMapWithExpectedSize(receivers.size());
                receivers.forEach((uid, sidSet) ->{
                    Map<String, ConnClient> sessionClientMap = userClientMap.get(uid);
                    if(sessionClientMap != null) {
                        for(String sid : sidSet) {
                            ConnClient client = sessionClientMap.get(sid);
                            if(client != null) {
                                client.sendPacket(packet);
                                Set<String> successSidSet = successReceivers.computeIfAbsent(uid, k -> Sets.newHashSetWithExpectedSize(sidSet.size()));
                                successSidSet.add(sid);
                            }
                        }
                    }
                });
                return successReceivers;
            }

            public void sendPacketLocal(List<Long> uids, Packet packet){
                logger.debug("send local packet:{} ,uids:{}", packet, uids);
                Map<Long, Set<String>> successReceivers = Maps.newHashMapWithExpectedSize(uids.size());
                uids.forEach(uid ->{
                    Map<String, ConnClient> sessionClientMap = userClientMap.get(uid);
                    if(sessionClientMap != null){
                        sessionClientMap.forEach((sid, client) ->{
                            client.sendPacket(packet);
                        });
                    }

                });
            }

            @Override
            public void sendPacket(long userId, Packet packet){
                logger.debug("sendPacket packet:{},userId:{}", packet, userId);
                Map<String, ConnClient> sessionClientMap = userClientMap.get(userId);
                Set<String> busIgnoreSids = Sets.newHashSet();
                if(sessionClientMap != null) {
                    sessionClientMap.values().forEach(client ->{
                        client.sendPacket(packet);
                    });
                    busIgnoreSids.addAll(sessionClientMap.keySet());
                }
                Receiver otherReceiver = Receiver.newReceiverIgnore(userId, busIgnoreSids);
                webSocketAPI.sendPacket(Lists.newArrayList(otherReceiver), packet, successReceivers ->{
                    logger.info("successReceivers:{}", successReceivers);
                });
            }

            @Override
            public void sendPacketIgnore(long userId, Packet packet, Collection<String> ignoreSids){
                logger.debug("sendPacketIgnore, packet:{},userId:{},ignoreSids:{}", packet, userId, ignoreSids);
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
                webSocketAPI.sendPacket(Lists.newArrayList(otherReceiver), packet, successReceivers ->{
                    logger.info("successReceivers:{}", successReceivers);
                });
            }

            @Override
            public void sendPacket(Collection<Receiver> receivers, Packet packet){
                logger.debug("send packet:{},receivers:{}", packet, receivers);
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
                webSocketAPI.sendPacket(otherReceivers, packet, successReceivers ->{
                    logger.info("successReceivers:{}", successReceivers);
                });
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
                }, false, res -> resultHandler.handle(res.result()));

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
                message.setMessageId(IdUtils.nextLong());
                message.setFromId(client.getUserId());
                message.setTimestamp(System.currentTimeMillis());
                Packet msgPacket = Packet.newPacket(message.getTimestamp(), Packet.DataType.MESSAGE, message);
                vertx.executeBlocking(f ->{
                    if (message.getToType() == Message.ToType.GROUP.ordinal()) {
                        List<Long> memberIds = userGroupService.groupMemberIdList(message.getToId());
                        List<Receiver> receivers = Lists.transform(memberIds, memberId ->{
                            if (memberId == client.getUserId()) {
                                return Receiver.newReceiverIgnore(memberId, Lists.newArrayList(client.getSessionId()));
                            } else {
                                return Receiver.newReceiver(memberId);
                            }
                        });
                        sendPacket(receivers, msgPacket);
                    } else {
                        sendPacket(message.getToId(), msgPacket);
                        sendPacketIgnore(message.getFromId(), msgPacket, Lists.newArrayList(client.getSessionId()));
                    }
                    messageService.addMsg(message);
                    f.complete(null);
                },res -> {});

            }

        };
    }

}
