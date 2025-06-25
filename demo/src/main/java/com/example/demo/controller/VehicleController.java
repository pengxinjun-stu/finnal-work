package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.VehicleRequest;
import com.example.demo.entity.Vehicle;
import com.example.demo.service.VehicleService;
import com.example.demo.validation.CreateValidationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;

    /**
     * 添加新车辆
     * POST /api/vehicles
     * 
     * 请求体示例:
     * {
     *   "frameNumber": "FRAME001",
     *   "batteryType": "TERNARY",  // TERNARY: 三元锂电池, LITHIUM_IRON: 磷酸铁锂电池
     *   "totalMileage": 1000.5,
     *   "batteryHealth": 98.5,
     *   "regionCode": "BJ"
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Vehicle>> createVehicle(@Valid @RequestBody VehicleRequest request) {
        logger.info("Received vehicle creation request: {}", request);
        try {
            Vehicle vehicle = vehicleService.createVehicle(request);
            logger.info("Vehicle created successfully: {}", vehicle);
            return ResponseEntity.ok(ApiResponse.success(vehicle));
        } catch (IllegalArgumentException e) {
            String errorMessage = "无效的请求参数：" + e.getMessage() + 
                "\n支持的电池类型：TERNARY(三元锂电池) 或 LITHIUM_IRON(磷酸铁锂电池)";
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
        }
    }

    /**
     * 更新车辆信息
     * PUT /api/vehicles/{vid}
     */
    @PutMapping("/{vid}")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(
            @PathVariable String vid,
            @Validated @RequestBody VehicleRequest request) {
        logger.info("Received vehicle update request for vid {}: {}", vid, request);
        try {
            Vehicle vehicle = vehicleService.updateVehicle(vid, request);
            logger.info("Vehicle updated successfully: {}", vehicle);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle updated successfully", vehicle));
        } catch (Exception e) {
            logger.error("Error updating vehicle: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除车辆
     * DELETE /api/vehicles/{vid}
     */
    @DeleteMapping("/{vid}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable String vid) {
        try {
            vehicleService.deleteVehicle(vid);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting vehicle: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取所有车辆
     * GET /api/vehicles
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllVehicles() {
        return ResponseEntity.ok(ApiResponse.success(vehicleService.getAllVehicles()));
    }

    /**
     * 根据ID获取车辆
     * GET /api/vehicles/{vid}
     */
    @GetMapping("/{vid}")
    public ResponseEntity<ApiResponse<Vehicle>> getVehicleById(@PathVariable String vid) {
        return vehicleService.getVehicleById(vid)
                .map(vehicle -> ResponseEntity.ok(ApiResponse.success(vehicle)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据地区代码获取车辆
     * GET /api/vehicles/region/{regionCode}
     */
    @GetMapping("/region/{regionCode}")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getVehiclesByRegion(@PathVariable String regionCode) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByRegion(regionCode);
        return ResponseEntity.ok(ApiResponse.success(vehicles));
    }

    /**
     * 分页获取所有车辆
     * GET /api/vehicles?page=0&size=10&sort=createTime,desc
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<Vehicle>>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime,desc") String sort) {
        try {
            logger.info("Fetching vehicles with page={}, size={}, sort={}", page, size, sort);
            
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            Page<Vehicle> vehicles = vehicleService.getAllVehicles(pageRequest);
            
            logger.info("Found {} vehicles in page {} of {}", 
                vehicles.getNumberOfElements(), vehicles.getNumber(), vehicles.getTotalPages());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Vehicles retrieved successfully", vehicles));
        } catch (Exception e) {
            logger.error("Error retrieving vehicles: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据区域代码查询车辆
     * GET /api/vehicles/region/{regionCode}?page=0&size=10
     */
    @GetMapping("/region/{regionCode}/all")
    public ResponseEntity<ApiResponse<Page<Vehicle>>> getVehiclesByRegion(
            @PathVariable String regionCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Fetching vehicles for region {} with page={}, size={}", regionCode, page, size);
            
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Vehicle> vehicles = vehicleService.getVehiclesByRegion(regionCode, pageRequest);
            
            logger.info("Found {} vehicles in region {} in page {} of {}", 
                vehicles.getNumberOfElements(), regionCode, vehicles.getNumber(), vehicles.getTotalPages());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Vehicles retrieved successfully", vehicles));
        } catch (Exception e) {
            logger.error("Error retrieving vehicles by region: {}", e.getMessage(), e);
            throw e;
        }
    }
} 