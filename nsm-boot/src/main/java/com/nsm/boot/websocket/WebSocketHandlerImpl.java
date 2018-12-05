package com.nsm.boot.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by nieshuming on 2018/9/20
 */

public class WebSocketHandlerImpl extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("new session {}", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("new msg {}", message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("session {} on error", session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("close session {}, status {}", session, status);
    }
}
