package com.example.demo.service;

import com.example.demo.entity.BatterySignal;
import com.example.demo.entity.Vehicle;
import com.example.demo.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalGeneratorService {

    private final VehicleRepository vehicleRepository;
    private final BatterySignalService batterySignalService;
    private final Random random = new Random();

    @Scheduled(fixedRate = 5000)  // 每5秒执行一次
    public void generateSignals() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        int count = 0;
        
        for (Vehicle vehicle : vehicles) {
            try {
                BatterySignal signal = generateSignal(vehicle);
                batterySignalService.processSignal(signal);
                count++;
            } catch (Exception e) {
                log.error("Error generating signal for vehicle {}: {}", vehicle.getVid(), e.getMessage());
            }
        }
        
        log.info("时间：{}，生成数据量：{}", LocalDateTime.now(), count);
    }

    private BatterySignal generateSignal(Vehicle vehicle) {
        BatterySignal signal = new BatterySignal();
        signal.setVid(vehicle.getVid());

        // 80%概率生成正常数据，20%概率生成异常数据
        boolean isNormal = random.nextDouble() < 0.8;

        double maxVoltage, minVoltage, maxCurrent, minCurrent;
        if (vehicle.getBatteryType() == Vehicle.BatteryType.TERNARY) {
            if (isNormal) {
                // 三元锂电池正常工作电压范围：3.0V-4.2V
                maxVoltage = 3.8 + random.nextDouble() * 0.4;  // 3.8-4.2V
                minVoltage = maxVoltage - random.nextDouble() * 0.2;  // 差值<0.2V，确保大于3.0V
                maxCurrent = 2.0 + random.nextDouble() * 0.2;  // 2.0-2.2A
                minCurrent = maxCurrent - random.nextDouble() * 0.2;  // 差值<0.2A
            } else {
                // 异常数据：
                // 电压差：0.2-6V
                // 电流差：0.2-2.5A
                maxVoltage = 3.8 + random.nextDouble() * 0.4;  // 3.8-4.2V
                double voltageDiff = 0.2 + random.nextDouble() * 5.8;  // 0.2-6.0V的差值
                minVoltage = Math.max(3.0, maxVoltage - voltageDiff);  // 确保最小电压不低于3.0V

                maxCurrent = 2.0 + random.nextDouble() * 1.0;  // 2.0-3.0A
                double currentDiff = 0.2 + random.nextDouble() * 2.3;  // 0.2-2.5A的差值
                minCurrent = Math.max(0.1, maxCurrent - currentDiff);  // 确保最小电流不低于0.1A
            }
        } else {  // LITHIUM_IRON
            if (isNormal) {
                // 磷酸铁锂电池正常工作电压范围：2.5V-3.65V
                maxVoltage = 3.2 + random.nextDouble() * 0.2;  // 3.2-3.4V
                minVoltage = maxVoltage - random.nextDouble() * 0.2;  // 差值<0.2V，确保大于2.5V
                maxCurrent = 1.5 + random.nextDouble() * 0.2;  // 1.5-1.7A
                minCurrent = maxCurrent - random.nextDouble() * 0.2;  // 差值<0.2A
            } else {
                // 异常数据：
                // 电压差：0-3V
                // 电流差：0.2-1.2A
                maxVoltage = 3.2 + random.nextDouble() * 0.45;  // 3.2-3.65V
                double voltageDiff = random.nextDouble() * 3.0;  // 0-3.0V的差值
                minVoltage = Math.max(2.5, maxVoltage - voltageDiff);  // 确保最小电压不低于2.5V

                maxCurrent = 1.5 + random.nextDouble() * 0.5;  // 1.5-2.0A
                double currentDiff = 0.2 + random.nextDouble();  // 0.2-1.2A的差值
                minCurrent = Math.max(0.1, maxCurrent - currentDiff);  // 确保最小电流不低于0.1A
            }
        }

        signal.setMaxVoltage(maxVoltage);
        signal.setMinVoltage(minVoltage);
        signal.setMaxCurrent(maxCurrent);
        signal.setMinCurrent(minCurrent);
        signal.setCreateTime(LocalDateTime.now());

        return signal;
    }
} 