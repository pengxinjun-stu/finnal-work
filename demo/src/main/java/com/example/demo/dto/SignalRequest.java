package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class SignalRequest {
    @NotNull
    private Integer carId;  // 车架编号
    
    private Integer warnId;  // 规则编号
    
    @NotNull
    private String signal;  // 信号JSON字符串
} 