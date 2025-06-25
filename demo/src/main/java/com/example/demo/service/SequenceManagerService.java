package com.example.demo.service;

import com.example.demo.entity.SequenceManager;
import com.example.demo.repository.SequenceManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SequenceManagerService {
    private static final String BRAND_PREFIX = "SU7";
    private static final String SEQUENCE_FORMAT = "%05d";
    private static final int MAX_SEQUENCE = 99999;
    
    private final SequenceManagerRepository sequenceManagerRepository;

    /**
     * 生成车辆ID
     * 格式：SU7N1{regionCode}{batchNumber}{sequence}
     * 例如：SU7N1BJ0001000001
     */
    @Transactional
    public String generateVehicleId(String regionCode) {
        String batchNumber = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        
        // 获取或创建序列管理器
        SequenceManager manager = sequenceManagerRepository.findByRegionAndBatchWithLock(regionCode, batchNumber)
            .orElseGet(() -> {
                SequenceManager newManager = new SequenceManager();
                newManager.setRegionCode(regionCode);
                newManager.setBatchNumber(batchNumber);
                newManager.setCurrentSequence(0L);
                return newManager;
            });
        
        // 增加序列号
        long nextSequence = manager.getCurrentSequence() + 1;
        if (nextSequence > MAX_SEQUENCE) {
            throw new IllegalStateException("Sequence number exceeded maximum value for the batch");
        }
        
        manager.setCurrentSequence(nextSequence);
        sequenceManagerRepository.save(manager);
        
        // 格式化序列号为5位数字
        String formattedSequence = String.format(SEQUENCE_FORMAT, nextSequence);
        
        // 返回完整的车辆ID
        return BRAND_PREFIX + "N1" + regionCode + batchNumber + formattedSequence;
    }
} 