package com.adroitfirm.rydo.location.dto;

import com.adroitfirm.rydo.location.model.Coordinate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverAvailabilityResponse {
	Coordinate coordinate;
	private double radiusKm;
	private String driverId;
}
