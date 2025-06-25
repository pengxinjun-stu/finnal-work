package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "warning_rule")
public class WarningRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 序号

    @Column(name = "rule_number", nullable = false)
    private Integer ruleNumber;  // 规则编号

    @Column(nullable = false)
    private String name;  // 规则名称

    @Column(name = "battery_type", nullable = false)
    private String batteryType;  // 电池类型：TERNARY-三元电池，LITHIUM_IRON-铁锂电池

    @Column(name = "warning_rule", nullable = false, columnDefinition = "TEXT")
    private String warningRule;  // 预警规则（JSON格式）
} 