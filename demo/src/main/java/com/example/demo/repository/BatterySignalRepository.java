package com.example.demo.repository;

import com.example.demo.entity.BatterySignal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BatterySignalRepository extends JpaRepository<BatterySignal, Long> {
    List<BatterySignal> findByVidOrderByCreateTimeDesc(String vid);
    List<BatterySignal> findByCreateTimeAfter(LocalDateTime time);
} 