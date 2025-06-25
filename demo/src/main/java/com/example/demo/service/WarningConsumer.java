package com.example.demo.service;

import com.example.demo.config.RocketMQConfig;
import com.example.demo.entity.BatterySignal;
import com.example.demo.entity.Vehicle;
import com.example.demo.entity.WarningRecord;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.WarningRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = RocketMQConfig.TOPIC_NAME,
    consumerGroup = RocketMQConfig.CONSUMER_GROUP
)
public class WarningConsumer implements RocketMQListener<BatterySignal> {

    private final VehicleRepository vehicleRepository;
    private final WarningRuleService warningRuleService;
    private final WarningRecordRepository warningRecordRepository;

    @Override
    @Transactional
    public void onMessage(BatterySignal signal) {
        try {
            // 1. 获取车辆信息
            Vehicle vehicle = vehicleRepository.findById(signal.getVid())
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + signal.getVid()));

            // 2. 构建信号Map
            Map<String, Double> signalMap = new HashMap<>();
            signalMap.put("Mx", signal.getMaxVoltage());
            signalMap.put("Mi", signal.getMinVoltage());
            signalMap.put("Ix", signal.getMaxCurrent());
            signalMap.put("Ii", signal.getMinCurrent());

            // 3. 检查电压差报警
            checkAndSaveWarning(vehicle, signalMap, 1, "电压差报警");

            // 4. 检查电流差报警
            checkAndSaveWarning(vehicle, signalMap, 2, "电流差报警");

        } catch (Exception e) {
            log.error("Error processing signal: " + e.getMessage());
        }
    }

    private void checkAndSaveWarning(Vehicle vehicle, Map<String, Double> signalMap, 
                                   Integer ruleNumber, String warningName) {
        Integer warningLevel = warningRuleService.calculateWarningLevel(
            vehicle.getBatteryType(), 
            ruleNumber,
            signalMap
        );

        if (warningLevel >= 0) {
            WarningRecord record = new WarningRecord();
            record.setVid(vehicle.getVid());
            record.setRuleNumber(ruleNumber);
            record.setWarningLevel(warningLevel);
            record.setWarningMessage(String.format("%s - 等级 %d", warningName, warningLevel));
            warningRecordRepository.save(record);
        }
    }
} 