package com.example.demo.dto;

import com.example.demo.entity.Vehicle;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VehicleRequest {

    @NotBlank(message = "车架号不能为空")
    private String frameNumber;

    @NotNull(message = "电池类型不能为空")
    private Vehicle.BatteryType batteryType;  // TERNARY: 三元锂电池, LITHIUM_IRON: 磷酸铁锂电池

    @NotNull(message = "总里程不能为空")
    @DecimalMin(value = "0.0", message = "总里程不能小于0")
    private Double totalMileage;

    @NotNull(message = "电池健康度不能为空")
    @DecimalMin(value = "0.0", message = "电池健康度不能小于0")
    @DecimalMax(value = "100.0", message = "电池健康度不能大于100")
    private Double batteryHealth;

    @NotBlank(message = "地区代码不能为空")
    private String regionCode;
} 