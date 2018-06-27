package com.nsm.websocket.server;

import com.nsm.common.utils.JsonUtils;
import com.nsm.bean.ErrorCode;
import com.nsm.bean.message.Message;
import com.nsm.bean.message.RegInfo;
import com.nsm.bean.packet.Packet;
import com.nsm.bean.packet.Packet.DataType;
import com.nsm.websocket.server.service.WebSocketService;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/24.
 */
public class ConnClient{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private long userId;
    private String sessionId;
    private ServerWebSocket socket;
    private WebSocketService webSocketService;

    public ConnClient(WebSocketService webSocketService, ServerWebSocket socket){
        this.socket = socket;
        socket.closeHandler((vd) -> this.onClose());
        socket.exceptionHandler(this::onException);
        socket.frameHandler(this::onFrame);
        this.webSocketService = webSocketService;
    }

    public void authenticated(long userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public void unAuthenticated() {
        this.userId =0;
        this.sessionId = null;
    }

    public void sendPacket(Packet packet){
        if(socket != null) {
            socket.write(Buffer.buffer(JsonUtils.toJson(packet)));
        }
    }

    private void onFrame(WebSocketFrame frame){
        String packetStr = frame.textData();
        logger.info("server received packet: {}, from: {}", packetStr, socket.remoteAddress());
        Packet ack = new Packet();
        ack.setType(DataType.ACK.getValue());
        Handler<ErrorCode> ackHandler = res ->{
            ack.setData(res);
            socket.write(Buffer.buffer(JsonUtils.toJson(ack)));
        };
        try {
            Packet packet = Packet.parseFrom(packetStr);
            if(packet == null) {
                ackHandler.handle(ErrorCode.BAD_REQUEST);
            }else {
                ack.setId(packet.getId());
                DataType dataType = DataType.valueOf(packet.getType());
                if(dataType == null) {
                    ackHandler.handle(ErrorCode.BAD_REQUEST);
                    return;
                }
                switch (dataType){
                    case ACK:
                        //do nothing
                        break;
                    case REGISTER:
                        if(packet.getData() == null) {
                            ackHandler.handle(ErrorCode.BAD_REQUEST);
                            break;
                        }
                        RegInfo regInfo = (RegInfo)packet.getData();
                        webSocketService.register(this, regInfo, ackHandler);
                        break;
                    case UN_REGISTER:
                        webSocketService.unRegister(this, ackHandler);
                        break;
                    case MESSAGE:
                        if(packet.getData() != null) {
                            Message message = (Message)packet.getData();
                            webSocketService.sendMessage(this, message, ackHandler);
                        }
                        ack.setData(ErrorCode.OK);
                        break;
                    default:
                        break;
                }
            }
        }catch (Exception e){
            logger.error("parse frame to Packet error", e);
            ackHandler.handle(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void onClose(){
        logger.info("closed socket, remote: {}", socket.remoteAddress());
        if(userId > 0) {
            webSocketService.unRegister(this, res ->{});
        }
    }

    private void onException(Throwable e){
        logger.error("socket cause exception: {}", e.getMessage());
    }

    public long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ServerWebSocket getSocket() {
        return socket;
    }

}
