package com.example.demo.service;

import com.example.demo.config.RocketMQConfig;
import com.example.demo.entity.BatterySignal;
import com.example.demo.entity.Vehicle;
import com.example.demo.repository.BatterySignalRepository;
import com.example.demo.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatterySignalService {

    private final BatterySignalRepository batterySignalRepository;
    private final VehicleRepository vehicleRepository;
    private final RocketMQTemplate rocketMQTemplate;
    private final WarningRuleService warningRuleService;

    @Transactional
    public void processSignal(BatterySignal signal) {
        try {
            // 1. 保存信号数据
            batterySignalRepository.save(signal);

            // 2. 发送到消息队列进行预警检查
            rocketMQTemplate.convertAndSend(RocketMQConfig.TOPIC_NAME, signal);
        } catch (Exception e) {
            log.error("Error processing signal: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    @CacheEvict(value = "batterySignal", key = "#id")
    public void deleteSignal(Long id) {
        batterySignalRepository.deleteById(id);
    }

    @Transactional
    @CachePut(value = "batterySignal", key = "#signal.id")
    public BatterySignal updateSignal(BatterySignal signal) {
        if (!batterySignalRepository.existsById(signal.getId())) {
            throw new EntityNotFoundException("Battery signal not found with id: " + signal.getId());
        }
        return batterySignalRepository.save(signal);
    }

    @Cacheable(value = "batterySignal", key = "#id")
    public BatterySignal getSignalById(Long id) {
        return batterySignalRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Battery signal not found with id: " + id));
    }

    @Cacheable(value = "batterySignals", key = "#vid")
    public List<BatterySignal> getSignalsByVid(String vid) {
        // 验证车辆是否存在
        if (!vehicleRepository.existsById(vid)) {
            throw new EntityNotFoundException("Vehicle not found with vid: " + vid);
        }
        return batterySignalRepository.findByVidOrderByCreateTimeDesc(vid);
    }

    // 清除指定车辆的信号缓存
    @CacheEvict(value = "batterySignals", key = "#vid")
    public void clearSignalCache(String vid) {
        // 方法体为空，仅用于清除缓存
    }
} 