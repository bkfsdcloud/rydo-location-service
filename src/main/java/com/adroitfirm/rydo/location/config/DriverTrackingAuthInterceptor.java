package com.adroitfirm.rydo.location.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.adroitfirm.rydo.location.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DriverTrackingAuthInterceptor implements HandshakeInterceptor {
	
	@Autowired
	private ObjectMapper mapper; 
	
	@Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws IOException {
		
        List<String> driverIds = request.getHeaders().get("X-IDENTIFIER");
        if (driverIds != null && !driverIds.isEmpty()) {
            attributes.put("driverId", driverIds.get(0));
            return true;
        }
        String body = mapper.writeValueAsString(ApiResponse.error("Driver key missing"));
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        response.getBody().write(body.getBytes(StandardCharsets.UTF_8));
        response.flush();
        return false;
    }

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}

}
