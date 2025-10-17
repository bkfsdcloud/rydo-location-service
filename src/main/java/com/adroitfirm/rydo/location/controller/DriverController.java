package com.adroitfirm.rydo.location.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adroitfirm.rydo.location.entity.Driver;
import com.adroitfirm.rydo.location.service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService svc;

    public DriverController(DriverService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Driver> create(@RequestBody Driver d) {
        return ResponseEntity.ok(svc.create(d));
    }

    @GetMapping("/<built-in function id>")
    public ResponseEntity<Driver> get(@PathVariable Long id) {
        return svc.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Driver>> available() {
        return ResponseEntity.ok(svc.listAvailable());
    }

    @PatchMapping("/<built-in function id>/availability")
    public ResponseEntity<Driver> setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return ResponseEntity.ok(svc.updateAvailability(id, available));
    }

    @PatchMapping("/<built-in function id>/location")
    public ResponseEntity<Driver> updateLocation(@PathVariable Long id,
                                                 @RequestParam double lat,
                                                 @RequestParam double lng) {
        return ResponseEntity.ok(svc.updateLocation(id, lat, lng, OffsetDateTime.now()));
    }
}
