package com.adroitfirm.rydo.location.service;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.adroitfirm.rydo.location.entity.RideAssignment;
import com.adroitfirm.rydo.location.repository.RideAssignmentRepository;

@Service
public class RideAssignmentService {
    private final RideAssignmentRepository repo;

    public RideAssignmentService(RideAssignmentRepository repo) {
        this.repo = repo;
    }

    public RideAssignment createAssignment(RideAssignment a) {
        a.setStatus("PENDING");
        a.setCreatedAt(OffsetDateTime.now());
        return repo.save(a);
    }

    public RideAssignment respond(Long assignmentId, String response) {
        RideAssignment a = repo.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        if (!"PENDING".equals(a.getStatus())) {
            throw new RuntimeException("Assignment not pending");
        }
        if ("ACCEPT".equalsIgnoreCase(response)) a.setStatus("ACCEPTED");
        else if ("DENY".equalsIgnoreCase(response)) a.setStatus("DENIED");
        else a.setStatus("EXPIRED");
        return repo.save(a);
    }
}
