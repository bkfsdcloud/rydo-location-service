package com.adroitfirm.rydo.location.service;

import java.util.List;

import com.adroitfirm.rydo.location.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.location.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.location.model.DriverInfo;
import com.adroitfirm.rydo.location.model.RideInfo;

public interface RedisCacheService {

	public void updateDriverLocation(String driverId, double latitude, double longitude);
	public void removeDriver(String driverId);
	public List<DriverAvailabilityResponse> findNearbyDrivers(DriverAvailabilityDto availabilityDto);
	
	public void cacheDriverInfo(DriverInfo driverInfo);
    public DriverInfo getDriverInfo(String driverId);
    public void cacheRideInfo(RideInfo rideInfo);
    public RideInfo getRideInfo(String rideId);
}
