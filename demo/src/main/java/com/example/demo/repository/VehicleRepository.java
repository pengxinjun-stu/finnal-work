package com.example.demo.repository;

import com.example.demo.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    // 基础的CRUD方法由JpaRepository提供
    Optional<Vehicle> findFirstByVidStartingWithOrderByVidDesc(String vidPrefix);

    List<Vehicle> findByRegionCode(String regionCode);

    Page<Vehicle> findByRegionCode(String regionCode, Pageable pageable);
} 