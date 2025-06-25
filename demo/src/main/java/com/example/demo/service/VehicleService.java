package com.example.demo.service;

import com.example.demo.dto.VehicleRequest;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.SequenceManager;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.SequenceManagerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private static final String BRAND_PREFIX = "SU7";
    private static final String SEQUENCE_FORMAT = "%05d";
    private static final int MAX_SEQUENCE = 99999;
    
    private final VehicleRepository vehicleRepository;
    private final SequenceManagerService sequenceManagerService;

    /**
     * 创建新车辆
     */
    @Transactional
    public Vehicle createVehicle(VehicleRequest request) {
        logger.info("Creating new vehicle with request: {}", request);
        
        // 验证并标准化地区代码
        String regionCode = request.getRegionCode().trim().toUpperCase();
        if (regionCode.length() != 2) {
            throw new IllegalArgumentException("Region code must be exactly 2 characters");
        }
        
        String vid = sequenceManagerService.generateVehicleId(regionCode);
        Vehicle vehicle = new Vehicle();
        vehicle.setVid(vid);
        vehicle.setRegionCode(regionCode);
        vehicle.setCreateTime(LocalDateTime.now());
        vehicle.setBatteryHealth(request.getBatteryHealth());
        vehicle.setBatteryType(request.getBatteryType());
        vehicle.setFrameNumber(request.getFrameNumber());
        vehicle.setTotalMileage(request.getTotalMileage());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        logger.info("Created new vehicle: {}", savedVehicle);
        return savedVehicle;
    }

    /**
     * 更新车辆信息
     */
    @Transactional
    public Vehicle updateVehicle(String vid, VehicleRequest request) {
        logger.info("Updating vehicle {} with request: {}", vid, request);
        Vehicle vehicle = vehicleRepository.findById(vid)
            .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + vid));
        
        // 不允许修改VID和创建时间
        if (request.getBatteryType() != null) {
            vehicle.setBatteryType(request.getBatteryType());
        }
        if (request.getFrameNumber() != null) {
            vehicle.setFrameNumber(request.getFrameNumber());
        }
        if (request.getTotalMileage() != null) {
            vehicle.setTotalMileage(request.getTotalMileage());
        }
        if (request.getBatteryHealth() != null) {
            vehicle.setBatteryHealth(request.getBatteryHealth());
        }
        if (request.getRegionCode() != null) {
            vehicle.setRegionCode(request.getRegionCode());
        }
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        logger.info("Updated vehicle: {}", updatedVehicle);
        return updatedVehicle;
    }

    /**
     * 删除车辆
     */
    @Transactional
    public void deleteVehicle(String vid) {
        logger.info("Deleting vehicle: {}", vid);
        if (!vehicleRepository.existsById(vid)) {
            throw new EntityNotFoundException("Vehicle not found with id: " + vid);
        }
        vehicleRepository.deleteById(vid);
        logger.info("Deleted vehicle: {}", vid);
    }

    /**
     * 获取单个车辆信息
     */
    public Vehicle getVehicle(String vid) {
        logger.info("Fetching vehicle: {}", vid);
        Vehicle vehicle = vehicleRepository.findById(vid)
            .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + vid));
        logger.info("Found vehicle: {}", vehicle);
        return vehicle;
    }

    /**
     * 分页获取所有车辆
     */
    public Page<Vehicle> getAllVehicles(Pageable pageable) {
        logger.info("Fetching all vehicles with pageable: {}", pageable);
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        logger.info("Found {} vehicles in page {} of {}", 
            vehicles.getNumberOfElements(), vehicles.getNumber(), vehicles.getTotalPages());
        return vehicles;
    }

    /**
     * 根据区域代码查询车辆
     */
    public List<Vehicle> getVehiclesByRegion(String regionCode) {
        logger.info("Fetching vehicles for region: {}", regionCode);
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Region code must not be empty!");
        }
        return vehicleRepository.findByRegionCode(regionCode.trim().toUpperCase());
    }

    public Page<Vehicle> getVehiclesByRegion(String regionCode, Pageable pageable) {
        logger.info("Fetching vehicles for region {} with pageable: {}", regionCode, pageable);
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Region code must not be empty!");
        }
        return vehicleRepository.findByRegionCode(regionCode.trim().toUpperCase(), pageable);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(String vid) {
        return vehicleRepository.findById(vid);
    }
} 