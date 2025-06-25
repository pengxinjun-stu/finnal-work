package com.example.demo.repository;

import com.example.demo.entity.WarningRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarningRuleRepository extends JpaRepository<WarningRule, Long> {
    List<WarningRule> findByBatteryTypeAndRuleNumber(String batteryType, Integer ruleNumber);
    List<WarningRule> findByBatteryType(String batteryType);
} 