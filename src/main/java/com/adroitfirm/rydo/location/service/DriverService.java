package com.adroitfirm.rydo.location.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.adroitfirm.rydo.location.entity.Driver;
import com.adroitfirm.rydo.location.repository.DriverRepository;

@Service
public class DriverService {
    private final DriverRepository repo;

    public DriverService(DriverRepository repo) {
        this.repo = repo;
    }

    public Driver create(Driver d) {
        return repo.save(d);
    }

    public Optional<Driver> get(Long id) {
        return repo.findById(id);
    }

    public List<Driver> listAvailable() {
        return repo.findByAvailableTrue();
    }

    public Driver updateAvailability(Long id, boolean available) {
        Driver d = repo.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        d.setAvailable(available);
        return repo.save(d);
    }

    public Driver updateLocation(Long id, double lat, double lng, OffsetDateTime at) {
        Driver d = repo.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        d.setLatitude(lat);
        d.setLongitude(lng);
        d.setLocationUpdatedAt(at);
        return repo.save(d);
    }
}
