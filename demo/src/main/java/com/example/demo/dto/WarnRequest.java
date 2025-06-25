package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class WarnRequest {
    @NotNull(message = "车架编号不能为空")
    private Integer carId;
    
    private Integer warnId;
    
    @NotNull(message = "信号数据不能为空")
    private String signal;
} 