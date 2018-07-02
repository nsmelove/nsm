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
        Notification notification = new Notification();
                               notification.setContent("今天下午放假");
        Packet packet = Packet.newPacket(0, Packet.DataType.NOTICE, notification);
        WebSocketAPI.publish(Lists.newArrayList(15275247172961L, 1529916046421001L), packet);
//        Vertx.clusteredVertx(new VertxOptions(), res -> {
//            WebSocketAPI api = WebSocketAPI.create(res.result());
//            api.getDeployIdSet(deploySet -> {
//                System.out.println("deploySet:" + deploySet);
//                deploySet.forEach(deployId ->{
//                    api.getUsersSessionMap(deployId, uRes ->{
//                       uRes.result().entries(eRes -> {
//                           System.out.println("deployId:" + deployId + ", entries:" + eRes.result());
//                           eRes.result().forEach((uid, sidSet) ->{
//                               api.sendPacket(Lists.newArrayList(Receiver.newReceiver(uid)), Packet.newPacket(0, Packet.DataType.NOTICE, notification), null);
//                           });
//
//                       });
//                    });
//                });
//            });
//        });
    }
}
