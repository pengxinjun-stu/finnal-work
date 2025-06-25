package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "warning_record")
public class WarningRecord implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vid;  // 车辆识别码

    @Column(name = "rule_number", nullable = false)
    private Integer ruleNumber;  // 规则编号

    @Column(name = "warning_level", nullable = false)
    private Integer warningLevel;  // 预警等级

    @Column(name = "warning_message", nullable = false)
    private String warningMessage;  // 预警信息

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;  // 预警时间

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
} 