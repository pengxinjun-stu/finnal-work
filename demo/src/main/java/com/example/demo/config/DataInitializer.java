package com.example.demo.config;

import com.example.demo.entity.Vehicle;
import com.example.demo.entity.WarningRule;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.WarningRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WarningRuleRepository warningRuleRepository;

    @Override
    public void run(String... args) {
        // 初始化示例车辆数据
        if (vehicleRepository.count() == 0) {
            Vehicle vehicle1 = new Vehicle();
            vehicle1.setVid("SU7N1BJ0001000001");
            vehicle1.setFrameNumber("XMSU7BJ001000001");
            vehicle1.setBatteryType(Vehicle.BatteryType.TERNARY);
            vehicle1.setTotalMileage(100.0);
            vehicle1.setBatteryHealth(100.0);
            vehicle1.setRegionCode("BJ");
            vehicle1.setCreateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle1);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setVid("SU7N1SH0001000001");
            vehicle2.setFrameNumber("XMSU7SH001000001");
            vehicle2.setBatteryType(Vehicle.BatteryType.LITHIUM_IRON);
            vehicle2.setTotalMileage(600.0);
            vehicle2.setBatteryHealth(95.0);
            vehicle2.setRegionCode("SH");
            vehicle2.setCreateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle2);

            Vehicle vehicle3 = new Vehicle();
            vehicle3.setVid("SU7N1GZ0001000001");
            vehicle3.setFrameNumber("XMSU7GZ001000001");
            vehicle3.setBatteryType(Vehicle.BatteryType.TERNARY);
            vehicle3.setTotalMileage(300.0);
            vehicle3.setBatteryHealth(98.0);
            vehicle3.setRegionCode("GZ");
            vehicle3.setCreateTime(LocalDateTime.now());
            vehicleRepository.save(vehicle3);
        }

        // 初始化预警规则
        if (warningRuleRepository.count() == 0) {
            // 三元锂电池规则
            WarningRule rule1 = new WarningRule();
            rule1.setRuleNumber(1);
            rule1.setName("三元锂电池预警规则");
            rule1.setBatteryType("TERNARY");
            rule1.setWarningRule("battery_health < 80");
            warningRuleRepository.save(rule1);

            // 磷酸铁锂电池规则
            WarningRule rule2 = new WarningRule();
            rule2.setRuleNumber(2);
            rule2.setName("磷酸铁锂电池预警规则");
            rule2.setBatteryType("LITHIUM_IRON");
            rule2.setWarningRule("battery_health < 75");
            warningRuleRepository.save(rule2);
        }
    }
} 