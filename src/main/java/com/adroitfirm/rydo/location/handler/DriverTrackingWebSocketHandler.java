package com.adroitfirm.rydo.location.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.adroitfirm.rydo.location.model.SocketPayload;
import com.adroitfirm.rydo.location.service.RedisCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DriverTrackingWebSocketHandler implements WebSocketHandler {

	private ObjectMapper mapper;
	private RedisCacheService redisCacheService;
	
    public DriverTrackingWebSocketHandler(ObjectMapper mapper, RedisCacheService redisCacheService) {
		this.mapper = mapper;
		this.redisCacheService = redisCacheService;
	}

	private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
	public static final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	String driverId = (String) session.getAttributes().get("driverId");
        sessions.add(session);
        sessionMap.put(driverId, session);
        log.info("Driver[{}] ready to take duty ", driverId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String driverId = (String) session.getAttributes().get("driverId");
        SocketPayload payload = mapper.readValue((String)message.getPayload(), SocketPayload.class);
        
        redisCacheService.updateDriverLocation(driverId, payload.getCoordinate().getLat(), payload.getCoordinate().getLng());
        log.info("Driver[{}] coordinate updated ", driverId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	String driverId = (String) session.getAttributes().get("driverId");
        sessions.remove(session);
        sessionMap.remove(driverId);
        redisCacheService.removeDriver(driverId);
        log.info("Driver[{}] is offline ", driverId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
