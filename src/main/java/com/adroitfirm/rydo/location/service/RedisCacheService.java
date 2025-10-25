package com.adroitfirm.rydo.location.service;

import java.util.List;

import com.adroitfirm.rydo.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.model.DriverInfo;
import com.adroitfirm.rydo.model.RideInfo;

public interface RedisCacheService {

	public void updateDriverLocation(String driverId, double latitude, double longitude);
	public void removeDriver(String driverId);
	public List<DriverAvailabilityResponse> findNearbyDrivers(DriverAvailabilityDto availabilityDto);
	
	public void cacheDriverInfo(DriverInfo driverInfo);
    public DriverInfo getDriverInfo(String driverId);
    public void cacheRideInfo(RideInfo rideInfo);
    public RideInfo getRideInfo(String rideId);
}
