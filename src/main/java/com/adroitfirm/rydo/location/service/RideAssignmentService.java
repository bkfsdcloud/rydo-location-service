package com.adroitfirm.rydo.location.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.adroitfirm.rydo.enumeration.RideStatus;
import com.adroitfirm.rydo.location.entity.RideAssignment;
import com.adroitfirm.rydo.location.repository.RideAssignmentRepository;

@Service
public class RideAssignmentService {
    private final RideAssignmentRepository repo;

    public RideAssignmentService(RideAssignmentRepository repo) {
        this.repo = repo;
    }

    public RideAssignment createAssignment(RideAssignment a) {
        a.setStatus(RideStatus.PENDING.name());
        a.setCreatedAt(OffsetDateTime.now());
        return repo.save(a);
    }
    
    public Optional<RideAssignment> findAssignmentByUser(Long customerId) {
    	return repo.findAssignmentByUser(customerId);
    }

    public RideAssignment respond(Long assignmentId, String response) {
        RideAssignment a = repo.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        if (!RideStatus.PENDING.name().equals(a.getStatus())) {
            throw new RuntimeException("Assignment not pending");
        }
        if ("ACCEPT".equalsIgnoreCase(response)) a.setStatus(RideStatus.ACCEPTED.name());
        else if ("DENY".equalsIgnoreCase(response)) a.setStatus(RideStatus.DENIED.name());
        else a.setStatus(RideStatus.EXPIRED.name());
        return repo.save(a);
    }
}
