package com.nsm.bean.packet;

import com.nsm.bean.ErrorCode;
import com.nsm.bean.message.Message;
import com.nsm.bean.message.Notification;
import com.nsm.bean.message.Offline;
import com.nsm.bean.message.Online;
import com.nsm.bean.message.RegInfo;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nieshuming on 2018/6/25
 */
public class Packet {
    private static Logger logger = LoggerFactory.getLogger(Packet.class);

    private long id;
    private int type;
    private Object data;

    public enum DataType{
        ACK(0),
        REGISTER(1),
        UN_REGISTER(2),
        MESSAGE(10),
        NOTICE(100),
        ONLINE(101),
        OFFLINE(102),
        ;
        private int type;
        private DataType(int type){
            this.type = type;
        }
        public int getValue(){
            return this.type;
        }

        public static DataType valueOf(int type) {
            for(DataType msgType : DataType.values()) {
                if(msgType.type == type) {
                    return msgType;
                }
            }
            return null;
        }
    }

    public static Packet newPacket(long id, DataType type, Object data){
        Packet packet = new Packet();
        packet.setId(id);
        packet.setType(type.getValue());
        packet.setData(data);
        return packet;
    }

    public static Packet parseFrom(String json) {
        Packet packet = null;
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonObject(json);
        }catch (DecodeException e) {
            logger.error(e.getMessage());
        }
        if(jsonObject == null) {
            return packet;
        }
        Integer type = jsonObject.getInteger("type");
        if(type == null) {
            return packet;
        }
        DataType dataType = DataType.valueOf(type);
        if(dataType == null) {
            return packet;
        }
        packet = new Packet();
        Long id = jsonObject.getLong("id");
        packet.setId(id == null ? 0: id);
        packet.setType(type);
        JsonObject data = jsonObject.getJsonObject("data");
        if(data == null) {
            return packet;
        }
        try {
            switch (dataType) {
                case ACK:
                    Integer code = data.getInteger("code");
                    String msg = data.getString("msg");
                    packet.setData(new ErrorCode(code == null ? 0 : code, msg));
                    break;
                case REGISTER:
                    packet.setData(data.mapTo(RegInfo.class));
                    break;
                case UN_REGISTER:
                    break;
                case MESSAGE:
                    packet.setData(data.mapTo(Message.class));
                    break;
                case NOTICE:
                    packet.setData(data.mapTo(Notification.class));
                    break;
                case ONLINE:
                    packet.setData(data.mapTo(Online.class));
                    break;
                case OFFLINE:
                    packet.setData(data.mapTo(Offline.class));
                    break;
                default:
            }
        }catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
        return packet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
