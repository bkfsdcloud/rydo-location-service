package com.adroitfirm.rydo.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adroitfirm.rydo.location.entity.Ride;

public interface RideRepository extends JpaRepository<Ride, Long> {
}