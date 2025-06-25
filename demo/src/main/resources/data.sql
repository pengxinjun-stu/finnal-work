-- 清空现有数据

-- 插入车辆数据
INSERT INTO vehicle (vid, frame_number, battery_type, total_mileage, battery_health, region_code, create_time) VALUES
('VEH202401010001', 'FRAME001', 'TERNARY', 1000.5, 98.5, 'BJ', NOW()),
('VEH202401010002', 'FRAME002', 'LITHIUM_IRON', 2000.8, 97.2, 'SH', NOW()),
('VEH202401010003', 'FRAME003', 'TERNARY', 1500.3, 99.0, 'GZ', NOW()),
('VEH202401010004', 'FRAME004', 'LITHIUM_IRON', 3000.1, 96.8, 'BJ', NOW()),
('VEH202401010005', 'FRAME005', 'TERNARY', 800.2, 99.5, 'SH', NOW()); 