package com.example.demo.service;

import com.example.demo.entity.Vehicle;
import com.example.demo.entity.WarningRule;
import com.example.demo.repository.WarningRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WarningRuleService {

    private final WarningRuleRepository warningRuleRepository;
    private final ObjectMapper objectMapper;

    public Integer calculateWarningLevel(Vehicle.BatteryType batteryType, Integer ruleNumber, Map<String, Double> signal) {
        List<WarningRule> rules;
        if (ruleNumber != null) {
            rules = warningRuleRepository.findByBatteryTypeAndRuleNumber(batteryType.name(), ruleNumber);
        } else {
            rules = warningRuleRepository.findByBatteryType(batteryType.name());
        }

        int highestWarningLevel = -1; // -1 表示无预警
        for (WarningRule rule : rules) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> ruleMap = objectMapper.readValue(rule.getWarningRule(), Map.class);
                
                int level = evaluateRule(ruleMap, signal);
                if (level > -1 && level < highestWarningLevel || highestWarningLevel == -1) {
                    highestWarningLevel = level;
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing warning rule", e);
            }
        }
        
        return highestWarningLevel;
    }

    private int evaluateRule(Map<String, Object> ruleMap, Map<String, Double> signal) {
        double diff;
        if (ruleMap.containsKey("voltageDiff")) {
            diff = signal.get("Mx") - signal.get("Mi");
            return evaluateThresholds((Map<String, Object>) ruleMap.get("voltageDiff"), diff);
        } else if (ruleMap.containsKey("currentDiff")) {
            diff = signal.get("Ix") - signal.get("Ii");
            return evaluateThresholds((Map<String, Object>) ruleMap.get("currentDiff"), diff);
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private int evaluateThresholds(Map<String, Object> thresholds, double value) {
        for (Map.Entry<String, Object> entry : thresholds.entrySet()) {
            if (entry.getKey().equals("none")) {
                continue;
            }
            
            Map<String, Double> range = (Map<String, Double>) entry.getValue();
            Double min = range.get("min");
            Double max = range.get("max");
            
            if (min != null && max != null) {
                if (value >= min && value < max) {
                    return Integer.parseInt(entry.getKey());
                }
            } else if (min != null) {
                if (value >= min) {
                    return Integer.parseInt(entry.getKey());
                }
            } else if (max != null) {
                if (value < max) {
                    return -1; // 无预警
                }
            }
        }
        return -1;
    }
} 