package com.example.demo.repository;

import com.example.demo.entity.SequenceManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface SequenceManagerRepository extends JpaRepository<SequenceManager, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SequenceManager s WHERE s.regionCode = :regionCode AND s.batchNumber = :batchNumber")
    Optional<SequenceManager> findByRegionAndBatchWithLock(
        @Param("regionCode") String regionCode,
        @Param("batchNumber") String batchNumber
    );
} 