package com.adroitfirm.rydo.location.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.Map;

@Controller
public class TrackingController {

    // Clients send to /app/track, messages are broadcast to /topic/locations
    @MessageMapping("/track")
    @SendTo("/topic/locations")
    public Map<String, Object> track(Map<String, Object> payload) {
        // payload expected: { "driverId": 1, "lat": 12.3, "lng": 77.4, "ts": "..." }
        payload.put("receivedAt", OffsetDateTime.now().toString());
        return payload;
    }
}
