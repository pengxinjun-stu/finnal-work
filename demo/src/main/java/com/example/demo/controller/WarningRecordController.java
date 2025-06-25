package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.WarningRecord;
import com.example.demo.repository.WarningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warning")
@RequiredArgsConstructor
public class WarningRecordController {

    private final WarningRecordRepository warningRecordRepository;

    @GetMapping("/vehicle/{vid}")
    public ApiResponse<List<WarningRecord>> getWarningsByVid(@PathVariable String vid) {
        return ApiResponse.success(warningRecordRepository.findByVidOrderByCreateTimeDesc(vid));
    }
} 