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
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.adroitfirm.rydo.location.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.location.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.location.model.Coordinate;
import com.adroitfirm.rydo.location.service.RedisCacheService;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

	private final RedisTemplate<String, String> redisTemplate;
	private final GeoOperations<String, String> geoOps;
	private static final String DRIVER_LOCATION_KEY = "driver-locations";
	
	public RedisCacheServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.geoOps = redisTemplate.opsForGeo();
    }
	
	public void updateDriverLocation(String driverId, double latitude, double longitude) {
        geoOps.add(DRIVER_LOCATION_KEY, new RedisGeoCommands.GeoLocation<>(driverId,
            new Point(longitude, latitude)));
    }

    public void removeDriver(String driverId) {
        redisTemplate.opsForZSet().remove(DRIVER_LOCATION_KEY, driverId);
    }

    public List<DriverAvailabilityResponse> findNearbyDrivers(DriverAvailabilityDto availabilityDto) {
        List<DriverAvailabilityResponse> resultList = new ArrayList<>();

        // Create a circle(centerPoint, radius)
        Point center = new Point(availabilityDto.getCoordinate().getLng(), availabilityDto.getCoordinate().getLat());
        Distance radius = new Distance(availabilityDto.getRadiusKm(), Metrics.KILOMETERS);
        Circle within = new Circle(center, radius);

        // Search
        GeoResults<GeoLocation<String>> results = geoOps.radius(DRIVER_LOCATION_KEY, within);
        
        GeoResults<RedisGeoCommands.GeoLocation<String>> results1 =
                geoOps.radius("driver-locations", 
                		new Circle(new Point(availabilityDto.getCoordinate().getLng(), availabilityDto.getCoordinate().getLat()), 
                				new Distance(availabilityDto.getRadiusKm(), Metrics.KILOMETERS)));
        System.out.println(results1);

        if (results == null || results.getContent().isEmpty()) return resultList;

        int added = 0;
        for (GeoResult<GeoLocation<String>> geoResult : results) {
            if (availabilityDto.getCountLimit() > 0 && added >= availabilityDto.getCountLimit()) break;

            GeoLocation<String> location = geoResult.getContent();
            String driverId = location.getName();
            Point point = location.getPoint(); // point.getX() = longitude, getY() = latitude
            if (point == null)
            	continue;
            
            double distanceKm = 0;
            // geoResult.getDistance() returns org.springframework.data.geo.Distance if included by the implementation;
            // with .radius(Circle) the distance may or may not be present depending on RedisTemplate version.
            if (geoResult.getDistance() != null) {
            	distanceKm = geoResult.getDistance().getValue();
            } else {
                // fallback: compute basic haversine (optional) or omit
            	distanceKm = computeHaversine(availabilityDto.getCoordinate().getLat(), availabilityDto.getCoordinate().getLng(), point.getY(), point.getX());
            }
            resultList.add(DriverAvailabilityResponse.builder()
            		.coordinate(Coordinate.builder().lat(point.getY()).lng(point.getX()).build())
            		.driverId(driverId).radiusKm(distanceKm).build());
            added++;
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
