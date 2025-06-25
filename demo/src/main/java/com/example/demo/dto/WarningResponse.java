package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class WarningResponse {
    private Integer carId;  // 车架编号
    private String batteryType;  // 电池类型
    private String warnName;  // 预警名称
    private Integer warnLevel;  // 预警等级
} 