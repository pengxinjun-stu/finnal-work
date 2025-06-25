package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.BatterySignal;
import com.example.demo.service.BatterySignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/battery/signal")
@RequiredArgsConstructor
public class BatterySignalController {

    private final BatterySignalService batterySignalService;

    @PostMapping
    public ApiResponse<BatterySignal> create(@RequestBody @Valid BatterySignal signal) {
        return ApiResponse.success(batterySignalService.createSignal(signal));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        batterySignalService.deleteSignal(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<BatterySignal> update(@PathVariable Long id, @RequestBody @Valid BatterySignal signal) {
        signal.setId(id);
        return ApiResponse.success(batterySignalService.updateSignal(signal));
    }

    @GetMapping("/{id}")
    public ApiResponse<BatterySignal> getById(@PathVariable Long id) {
        return ApiResponse.success(batterySignalService.getSignalById(id));
    }

    @GetMapping("/vehicle/{vid}")
    public ApiResponse<List<BatterySignal>> getByVid(@PathVariable String vid) {
        return ApiResponse.success(batterySignalService.getSignalsByVid(vid));
    }
} 