package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vehicle")
public class Vehicle implements Serializable {
    
    @Id
    @Column(length = 16)
    private String vid;  // 车辆识别码，16位随机字符串

    @Column(name = "frame_number", nullable = false)
    private String frameNumber;  // 车架编号

    @Enumerated(EnumType.STRING)
    @Column(name = "battery_type", nullable = false)
    private BatteryType batteryType;  // 电池类型

    @Column(name = "total_mileage", nullable = false)
    private Double totalMileage;  // 总里程(km)

    @Column(name = "battery_health", nullable = false)
    private Double batteryHealth;  // 电池健康状态(%)

    @Column(name = "region_code", nullable = false, length = 2)
    private String regionCode;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    public enum BatteryType {
        TERNARY("三元锂电池"),    // 三元锂电池
        LITHIUM_IRON("磷酸铁锂电池");  // 磷酸铁锂电池

        private final String description;

        BatteryType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 