package com.example.demo.service;

import com.example.demo.dto.WarnRequest;
import com.example.demo.dto.WarnResponse;
import com.example.demo.entity.Vehicle;
import com.example.demo.repository.VehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BatteryWarningService {

    private final VehicleRepository vehicleRepository;
    private final WarningRuleService warningRuleService;
    private final ObjectMapper objectMapper;

    public List<WarnResponse> processWarnings(List<WarnRequest> requests) {
        List<WarnResponse> responses = new ArrayList<>();
        
        for (WarnRequest request : requests) {
            try {
                // 1. 查找车辆信息
                Vehicle vehicle = vehicleRepository.findById(request.getCarId().toString())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + request.getCarId()));

                // 2. 解析信号数据
                @SuppressWarnings("unchecked")
                Map<String, Double> signalMap = objectMapper.readValue(request.getSignal(), Map.class);

                // 3. 如果指定了规则编号，只检查该规则
                if (request.getWarnId() != null) {
                    checkAndAddWarning(responses, vehicle, signalMap, request.getWarnId());
                } else {
                    // 4. 否则检查所有规则
                    checkAndAddWarning(responses, vehicle, signalMap, 1); // 检查电压差报警
                    checkAndAddWarning(responses, vehicle, signalMap, 2); // 检查电流差报警
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing signal data", e);
            } catch (EntityNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        return responses;
    }

    private void checkAndAddWarning(List<WarnResponse> responses, Vehicle vehicle, 
                                  Map<String, Double> signalMap, Integer ruleNumber) {
        Integer warningLevel = warningRuleService.calculateWarningLevel(
            vehicle.getBatteryType(),
            ruleNumber,
            signalMap
        );

        if (warningLevel >= 0) {
            responses.add(WarnResponse.builder()
                .carId(Integer.parseInt(vehicle.getFrameNumber()))
                .batteryType(vehicle.getBatteryType().getDescription())
                .warnName(ruleNumber == 1 ? "电压差报警" : "电流差报警")
                .warnLevel(warningLevel)
                .build());
        }
    }
} 