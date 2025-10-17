package com.adroitfirm.rydo.location.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adroitfirm.rydo.location.entity.RideAssignment;
import com.adroitfirm.rydo.location.service.RideAssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class RideAssignmentController {
    private final RideAssignmentService svc;

    public RideAssignmentController(RideAssignmentService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<RideAssignment> create(@RequestBody RideAssignment a) {
        return ResponseEntity.ok(svc.createAssignment(a));
    }

    @PostMapping("/<built-in function id>/respond")
    public ResponseEntity<RideAssignment> respond(@PathVariable Long id, @RequestParam String action) {
        return ResponseEntity.ok(svc.respond(id, action));
    }
}
