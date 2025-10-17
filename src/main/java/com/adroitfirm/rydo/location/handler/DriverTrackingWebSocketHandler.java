package com.adroitfirm.rydo.location.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.adroitfirm.rydo.location.model.SocketPayload;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DriverTrackingWebSocketHandler implements WebSocketHandler {

	private ObjectMapper mapper;
	
    public DriverTrackingWebSocketHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Received: " + message.getPayload());
        SocketPayload payload = mapper.readValue((String)message.getPayload(), SocketPayload.class);
        
        broadcast(payload);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void broadcast(SocketPayload payload) {
        synchronized (sessions) {
            sessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(payload.getAction()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
