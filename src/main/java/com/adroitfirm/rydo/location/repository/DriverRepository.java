package com.adroitfirm.rydo.location.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adroitfirm.rydo.location.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByAvailableTrue();
}
