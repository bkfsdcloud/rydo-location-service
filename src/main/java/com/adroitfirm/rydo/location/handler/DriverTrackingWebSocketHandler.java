package com.adroitfirm.rydo.location.handler;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.adroitfirm.rydo.dto.SocketMessage;
import com.adroitfirm.rydo.model.DriverInfo;
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

	public static final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	String driverId = (String) session.getAttributes().get("driverId");
        sessionMap.put(driverId, session);
        log.info("Driver[{}] ready to take duty ", driverId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String driverId = (String) session.getAttributes().get("driverId");
        SocketMessage payload = mapper.readValue((String)message.getPayload(), SocketMessage.class);
        
        if (Objects.nonNull(payload.getRideStatus())) {
        	
        }
        redisCacheService.cacheDriverInfo(DriverInfo.builder().driverId(driverId).coordinate(payload.getDriverLocation()).status(payload.getDriverStatus()).build());
        log.info("Driver[{}] status updated - [{}] ", driverId, payload.getDriverStatus());
        redisCacheService.updateDriverLocation(driverId, payload.getDriverLocation().getLat(), payload.getDriverLocation().getLng());
        log.info("Driver[{}] live coordinate updated ", driverId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	String driverId = (String) session.getAttributes().get("driverId");
        sessionMap.remove(driverId);
//        redisCacheService.removeDriver(driverId);
        log.info("Driver[{}] is offline ", driverId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
