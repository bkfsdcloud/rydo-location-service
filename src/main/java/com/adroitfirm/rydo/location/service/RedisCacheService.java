package com.adroitfirm.rydo.location.service;

import java.util.List;

import com.adroitfirm.rydo.location.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.location.dto.DriverAvailabilityResponse;

public interface RedisCacheService {

	public void updateDriverLocation(String driverId, double latitude, double longitude);
	public void removeDriver(String driverId);
	public List<DriverAvailabilityResponse> findNearbyDrivers(DriverAvailabilityDto availabilityDto);
}
