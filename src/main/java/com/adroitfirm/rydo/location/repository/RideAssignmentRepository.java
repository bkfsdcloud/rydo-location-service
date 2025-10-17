package com.adroitfirm.rydo.location.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adroitfirm.rydo.location.entity.RideAssignment;

public interface RideAssignmentRepository extends JpaRepository<RideAssignment, Long> {
    List<RideAssignment> findByDriverIdAndStatus(Long driverId, String status);
}
