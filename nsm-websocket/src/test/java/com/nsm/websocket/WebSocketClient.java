package com.nsm.websocket;

import com.nsm.common.utils.JsonUtils;
import com.nsm.bean.message.Message;
import com.nsm.bean.message.RegInfo;
import com.nsm.bean.packet.Packet;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nieshuming on 2018/6/25
 */
public class WebSocketClient {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClient httpClient1 = vertx.createHttpClient();
        httpClient1.websocket(80, "192.168.0.129", "/", ws ->{
            ws.frameHandler(frame ->{
                System.out.println("client on msg:" + frame.textData());

                if(Packet.parseFrom(frame.textData()).getType() == Packet.DataType.MESSAGE.getValue()) {
                    vertx.setTimer(5000,tm ->{
                        Message message = new Message();
                        message.setToId(1529916046421001L);
                        message.setContent("你好啊，美女");
                        Packet packet = Packet.newPacket(System.currentTimeMillis(), Packet.DataType.MESSAGE, message);
                        ws.write(Buffer.buffer(JsonUtils.toJson(packet)));
                    });
                }

            });

            RegInfo regInfo = new RegInfo();
            regInfo.setUserId(1527524717296L);
            regInfo.setSessionId("e36cf3a0652cb6ae14f52a407a940893");
            Packet packet = Packet.newPacket(System.currentTimeMillis(), Packet.DataType.REGISTER, regInfo);
            ws.write(Buffer.buffer(JsonUtils.toJson(packet)));


        });

        HttpClient httpClient2 = vertx.createHttpClient();
        httpClient2.websocket(81, "192.168.0.129", "/", ws ->{
            ws.frameHandler(frame ->{
                System.out.println("client on msg:" + frame.textData());
                if(Packet.parseFrom(frame.textData()).getType() == Packet.DataType.MESSAGE.getValue()){
                    vertx.setTimer(5000, tm ->{
                        Message message = new Message();
                        message.setToId(1527524717296L);
                        message.setContent("你好啊，帅哥");
                        Packet packet = Packet.newPacket(System.currentTimeMillis(), Packet.DataType.MESSAGE, message);
                        ws.write(Buffer.buffer(JsonUtils.toJson(packet)));
                    });
                }
            });
            RegInfo regInfo = new RegInfo();
            regInfo.setUserId(1529916046421001L);
            regInfo.setSessionId("7a818a78024b14deac416def8eef1a36");
            Packet packet = Packet.newPacket(System.currentTimeMillis(), Packet.DataType.REGISTER, regInfo);
            ws.write(Buffer.buffer(JsonUtils.toJson(packet)));
            vertx.setTimer(1000, tm ->{
                Message message = new Message();
                message.setToId(1527524717296L);
                message.setContent("你好啊，帅哥");
                Packet packet1 = Packet.newPacket(System.currentTimeMillis(), Packet.DataType.MESSAGE, message);
                ws.write(Buffer.buffer(JsonUtils.toJson(packet1)));
            });

        });
    }
}
