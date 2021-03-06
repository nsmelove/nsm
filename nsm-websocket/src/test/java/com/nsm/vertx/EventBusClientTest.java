package com.nsm.vertx;

import com.google.common.collect.Lists;
import com.nsm.bean.packet.Packet;
import com.nsm.websocket.WebSocketAPI;
import com.nsm.websocket.bean.Receiver;
import io.vertx.core.Vertx;

import java.util.List;

/**
 * Created by nieshuming on 2018/6/26.
 */
public class EventBusClientTest {

    public static void main(String[] args) {
        WebSocketAPI client = WebSocketAPI.create(Vertx.vertx());
        client.consume("111", (rec, packet) ->{
            System.out.println("rec:{}"+ rec+ " packet:" + packet);
            return null;
        });
        List<Receiver> receivers = Lists.newArrayList(
                Receiver.newReceiver(1111L),
                Receiver.newReceiver(22222L),
                Receiver.newReceiver(33333L),
                Receiver.newReceiver(44444L),
                Receiver.newReceiver(55555L),
                Receiver.newReceiver(66666L)
        );
        client.sendPacket(receivers, Packet.newPacket(0, Packet.DataType.ACK, null), null);

    }
}
