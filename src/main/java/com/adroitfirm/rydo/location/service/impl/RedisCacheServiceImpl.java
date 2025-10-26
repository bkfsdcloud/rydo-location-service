package com.adroitfirm.rydo.location.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.adroitfirm.rydo.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.model.Coordinate;
import com.adroitfirm.rydo.model.DriverInfo;
import com.adroitfirm.rydo.model.RideInfo;
import com.adroitfirm.rydo.location.service.RedisCacheService;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

	private static final String DRIVER_LOCATION_KEY = "driver-locations";
	private static final String DRIVER_INFO_KEY = "driver:info:";
	private static final String RIDE_INFO_KEY = "ride:info:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final GeoOperations<String, Object> geoOps;
	
	public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
        this.geoOps = redisTemplate.opsForGeo();
    }
	
	public void updateDriverLocation(String driverId, double latitude, double longitude) {
		Point point = new Point(longitude, latitude);
        geoOps.add(DRIVER_LOCATION_KEY, new RedisGeoCommands.GeoLocation<>(driverId, point));
    }

    public void removeDriver(String driverId) {
    	geoOps.remove(DRIVER_LOCATION_KEY, driverId);
    }
    
    public void cacheDriverInfo(DriverInfo driverInfo) {
    	redisTemplate.opsForValue().set(DRIVER_INFO_KEY + driverInfo.getDriverId(), driverInfo);
    }
    
    public DriverInfo getDriverInfo(String driverId) {
    	return (DriverInfo) redisTemplate.opsForValue().get(DRIVER_INFO_KEY + driverId);
    }
    
    public void cacheRideInfo(RideInfo rideInfo) {
    	redisTemplate.opsForValue().set(RIDE_INFO_KEY + rideInfo.getRideId(), rideInfo);
    }
    
    public RideInfo getRideInfo(String rideId) {
    	return (RideInfo) redisTemplate.opsForValue().get(RIDE_INFO_KEY + rideId);
    }

    public List<DriverAvailabilityResponse> findNearbyDrivers(DriverAvailabilityDto availabilityDto) {
        List<DriverAvailabilityResponse> resultList = new ArrayList<>();
        
        Point center = new Point(availabilityDto.getCoordinate().getLng(),
        		availabilityDto.getCoordinate().getLat());
        Distance radius = new Distance(availabilityDto.getRadiusKm(), Metrics.KILOMETERS);
        Circle within = new Circle(center, radius);
        
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates()
                .includeDistance()
                .sortAscending()
                .limit(availabilityDto.getCountLimit());
        GeoResults<GeoLocation<Object>> results = geoOps.radius(DRIVER_LOCATION_KEY, within, args);
        
        if (results == null || results.getContent().isEmpty()) return resultList;

        for (GeoResult<GeoLocation<Object>> geoResult : results) {
            GeoLocation<Object> location = geoResult.getContent();
            String driverId = (String) location.getName();
            Point point = location.getPoint(); // point.getX() = longitude, getY() = latitude
            if (point == null)
            	continue;
            
            double distanceKm = 0;
            if (geoResult.getDistance() != null) {
            	distanceKm = geoResult.getDistance().getValue();
            } else {
                // fallback: compute basic haversine (optional) or omit
            	distanceKm = computeHaversine(availabilityDto.getCoordinate().getLat(), availabilityDto.getCoordinate().getLng(), point.getY(), point.getX());
            }
            resultList.add(DriverAvailabilityResponse.builder()
            		.coordinate(Coordinate.builder().lat(point.getY()).lng(point.getX()).build())
            		.driverId(driverId).radiusKm(distanceKm).build());
        }
        return resultList;
    }
    
    private double computeHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
