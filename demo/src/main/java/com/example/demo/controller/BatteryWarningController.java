package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.WarnRequest;
import com.example.demo.dto.WarnResponse;
import com.example.demo.service.BatteryWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BatteryWarningController {

    private final BatteryWarningService batteryWarningService;

    @PostMapping("/warn")
    public ApiResponse<List<WarnResponse>> processWarning(@RequestBody @Valid List<WarnRequest> requests) {
        try {
            List<WarnResponse> responses = batteryWarningService.processWarnings(requests);
            return ApiResponse.success(responses);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
} 