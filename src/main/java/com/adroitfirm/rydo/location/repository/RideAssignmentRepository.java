package com.adroitfirm.rydo.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adroitfirm.rydo.location.entity.RideAssignment;

public interface RideAssignmentRepository extends JpaRepository<RideAssignment, Long> {
    List<RideAssignment> findByDriverIdAndStatus(Long driverId, String status);
    
    @Query("SELECT ra FROM RideAssignment ra JOIN Ride r ON (r.id = ra.rideId) WHERE ra.status = 'PENDING' and r.customer.id = :id")
	Optional<RideAssignment> findAssignmentByUser(@Param("id") Long id);
}
