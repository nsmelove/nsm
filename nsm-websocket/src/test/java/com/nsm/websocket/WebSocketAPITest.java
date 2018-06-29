package com.nsm.websocket;

import com.google.common.collect.Lists;
import com.nsm.bean.message.Notification;
import com.nsm.bean.packet.Packet;
import com.nsm.websocket.bean.Receiver;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by nieshuming on 2018/6/29
 */
public class WebSocketAPITest {

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), res -> {
            WebSocketAPI api = WebSocketAPI.create(res.result());
            api.getDeployIdSet(deploySet -> {
                System.out.println("deploySet:" + deploySet);
                deploySet.forEach(deployId ->{
                    api.getUsersSessionMap(deployId, uRes ->{
                       uRes.result().entries(eRes -> {
                           System.out.println("deployId:" + deployId + ", entries:" + eRes.result());
                           eRes.result().forEach((uid, sidSet) ->{
                               Notification notification = new Notification();
                               notification.setUserId(uid);
                               notification.setContent("今天下午放假");
                               api.sendPacket(Lists.newArrayList(Receiver.newReceiver(uid)), Packet.newPacket(0, Packet.DataType.NOTICE, notification), null);
                           });

                       });
                    });
                });
            });
        });
    }
}
