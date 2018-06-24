package com.nsm.vertx.websocket.message;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/24.
 */
public class Message{
    private long messageId;
    private int msgType;
    private long fromId;
    private String fromSid;
    private long toId;
    private long timestamp;

    public enum MsgType{
        REGISTER(1),
        UN_REGISTER(2);
        private int type;
        private MsgType(int type){
            this.type = type;
        }
        public int getValue(){
            return this.type;
        }

        public static MsgType valueOf(int type) {
            for(MsgType msgType : MsgType.values()) {
                if(msgType.type == type) {
                    return msgType;
                }
            }
            return null;
        }
    }

    public long getMessageId(){
        return messageId;
    }

    public void setMessageId(long messageId){
        this.messageId = messageId;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
