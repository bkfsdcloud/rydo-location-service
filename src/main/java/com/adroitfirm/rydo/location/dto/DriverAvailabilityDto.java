package com.adroitfirm.rydo.location.dto;

import com.adroitfirm.rydo.location.model.Coordinate;

import lombok.Data;

@Data
public class DriverAvailabilityDto {
	Coordinate coordinate;
	private double radiusKm;
	private int countLimit = 10;
}
