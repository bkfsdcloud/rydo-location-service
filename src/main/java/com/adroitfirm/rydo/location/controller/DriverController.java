package com.adroitfirm.rydo.location.controller;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.adroitfirm.rydo.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.location.entity.Driver;
import com.adroitfirm.rydo.location.handler.DriverTrackingWebSocketHandler;
import com.adroitfirm.rydo.location.service.DriverService;
import com.adroitfirm.rydo.location.service.RedisCacheService;
import com.adroitfirm.rydo.utility.ApiResponse;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverSvc;
    private final RedisCacheService cacheService;

    @PostMapping
    public ResponseEntity<Driver> create(@RequestBody Driver d) {
        return ResponseEntity.ok(driverSvc.create(d));
    }

    @GetMapping("/<built-in function id>")
    public ResponseEntity<Driver> get(@PathVariable Long id) {
        return driverSvc.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/available")
    public ResponseEntity<List<DriverAvailabilityResponse>> available(@RequestBody DriverAvailabilityDto availabilityDto) {
        return ResponseEntity.ok(cacheService.findNearbyDrivers(availabilityDto));
    }

    @PatchMapping("/<built-in function id>/availability")
    public ResponseEntity<Driver> setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return ResponseEntity.ok(driverSvc.updateAvailability(id, available));
    }

    @PatchMapping("/<built-in function id>/location")
    public ResponseEntity<Driver> updateLocation(@PathVariable Long id,
                                                 @RequestParam double lat,
                                                 @RequestParam double lng) {
        return ResponseEntity.ok(driverSvc.updateLocation(id, lat, lng, OffsetDateTime.now()));
    }
    
    @GetMapping("/inform")
    public ResponseEntity<ApiResponse<String>> inform(@RequestParam String message) throws Exception {
    	WebSocketSession session = DriverTrackingWebSocketHandler.sessionMap.get("6379099578");
    	
    	if (Objects.isNull(session))
    		throw new Exception("Session not created");
    	synchronized (session) {
    		if (!session.isOpen())
    			throw new Exception("Session closed");
    		try {
				session.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
        return ResponseEntity.ok(ApiResponse.success(message, "Notified"));
    }
}
