package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "battery_signal")
public class BatterySignal implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vid;  // 车辆识别码

    @Column(nullable = false)
    private Double maxVoltage;  // 最高电压 Mx

    @Column(nullable = false)
    private Double minVoltage;  // 最低电压 Mi

    @Column(nullable = false)
    private Double maxCurrent;  // 最高电流 Ix

    @Column(nullable = false)
    private Double minCurrent;  // 最低电流 Ii

    @Column(nullable = false)
    private LocalDateTime createTime;  // 信号创建时间

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
} 