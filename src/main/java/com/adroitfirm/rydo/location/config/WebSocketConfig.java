package com.adroitfirm.rydo.location.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.adroitfirm.rydo.location.handler.DriverTrackingWebSocketHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final DriverTrackingWebSocketHandler webSocketHandler;
	private final DriverTrackingAuthInterceptor authInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws")
        	.addInterceptors(authInterceptor)
            .setAllowedOriginPatterns("*");
    }
}
