package com.example.demo.repository;

import com.example.demo.entity.WarningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarningRecordRepository extends JpaRepository<WarningRecord, Long> {
    List<WarningRecord> findByVidOrderByCreateTimeDesc(String vid);
} 