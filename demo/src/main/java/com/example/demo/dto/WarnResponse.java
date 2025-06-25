package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarnResponse {
    @JsonProperty("车架编号")
    private Integer carId;
    
    @JsonProperty("电池类型")
    private String batteryType;
    
    @JsonProperty("warnName")
    private String warnName;
    
    @JsonProperty("warnLevel")
    private Integer warnLevel;
} 