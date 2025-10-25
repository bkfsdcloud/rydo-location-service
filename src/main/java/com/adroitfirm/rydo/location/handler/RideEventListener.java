package com.adroitfirm.rydo.location.handler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.adroitfirm.rydo.dto.DriverAvailabilityDto;
import com.adroitfirm.rydo.dto.DriverAvailabilityResponse;
import com.adroitfirm.rydo.dto.SocketMessage;
import com.adroitfirm.rydo.enumeration.RideStatus;
import com.adroitfirm.rydo.location.entity.Ride;
import com.adroitfirm.rydo.location.entity.RideAssignment;
import com.adroitfirm.rydo.location.repository.RideRepository;
import com.adroitfirm.rydo.location.service.RedisCacheService;
import com.adroitfirm.rydo.location.service.RideAssignmentService;
import com.adroitfirm.rydo.model.Coordinate;
import com.adroitfirm.rydo.model.RideInfo;
import com.adroitfirm.rydo.model.kafka.RideRequested;
import com.adroitfirm.rydo.utility.RideConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class RideEventListener {

	@Value("${ride.service.radius}")
	private double radiusInM;
	
	private final RideRepository rideRepository;
	private final RideAssignmentService rideAssignmentService;
	private final RedisCacheService redisCacheService;
	private final ObjectMapper mapper;
	
	public RideEventListener(RideRepository rideRepository, RedisCacheService redisCacheService, 
			ObjectMapper mapper, RideAssignmentService rideAssignmentService) {
		this.rideRepository = rideRepository;
		this.redisCacheService = redisCacheService;
		this.mapper = mapper;
		this.rideAssignmentService = rideAssignmentService;
	}
	
	@Transactional
	@KafkaListener(topicPattern = RideConstants.RIDE_REQUESTED_TOPIC, groupId = RideConstants.RIDE_REQUESTED_TOPIC + "-group-id")
	public void rideCreated(ConsumerRecord<String, RideRequested> consumerRecord) throws Exception {
		RideRequested rideRequested = consumerRecord.value();
		
		Ride ride = rideRepository.getReferenceById(rideRequested.getRideId());
		
		RideAssignment assignment = new RideAssignment();
		assignment.setDriverId(rideRequested.getDriverId());
		assignment.setRideId(rideRequested.getRideId());
		assignment.setStatus(RideStatus.PENDING.name());
		
		rideAssignmentService.createAssignment(assignment);
		
		Coordinate coordinate = Coordinate.builder().lat(ride.getPickupLat()).lng(ride.getPickupLng()).build();
		DriverAvailabilityDto driverAvailabilityDto = DriverAvailabilityDto.builder().coordinate(coordinate).radiusKm(radiusInM).build(); 
		
		List<DriverAvailabilityResponse> availabilityResponses = redisCacheService.findNearbyDrivers(driverAvailabilityDto);
		
		Optional<DriverAvailabilityResponse> availabilityResponseOpt = availabilityResponses.stream().max(Comparator.comparing(DriverAvailabilityResponse::getRadiusKm));
		
		if (availabilityResponseOpt.isPresent()) {
			
			DriverAvailabilityResponse availabilityResponse = availabilityResponseOpt.get();
			
			WebSocketSession session = DriverTrackingWebSocketHandler.sessionMap.get(availabilityResponse.getDriverId());
	    	
			SocketMessage message = SocketMessage.builder()
					.message("You have a ride")
					.distance(ride.getDistanceKm())
					.fare(ride.getFareEstimated())
					.pickup(coordinate)
					.drop(Coordinate.builder().lat(ride.getDropLat()).lng(ride.getDropLng()).build())
					.build();
			
			if (Objects.nonNull(ride.getDriver())) {
				RideInfo rideInfo = RideInfo.builder().driverId(ride.getDriver().getPhone())
						.rideId(ride.getId()).status(RideStatus.REQUESTED.name())
						.riderId(ride.getCustomer().getPhone()).build();
				
				redisCacheService.cacheRideInfo(rideInfo);
			}
			
	    	if (Objects.nonNull(session) && session.isOpen()) {
	    		synchronized (session) {
	    			try {
	    				session.sendMessage(new TextMessage(mapper.writeValueAsBytes(message)));
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    	}
			
		}
	}
}
