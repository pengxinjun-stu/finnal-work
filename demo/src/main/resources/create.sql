-- 创建车辆信息表


use xiaomi;

-- 删除外键约束
SET FOREIGN_KEY_CHECKS = 0;

-- 删除旧表
DROP TABLE IF EXISTS warning_record;
DROP TABLE IF EXISTS battery_signal;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS warning_rule;
DROP TABLE IF EXISTS sequence_manager;

-- 重新启用外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 车辆表
CREATE TABLE IF NOT EXISTS vehicle (
    vid VARCHAR(16) PRIMARY KEY,
    frame_number VARCHAR(17) NOT NULL,
    battery_type VARCHAR(20) NOT NULL,
    total_mileage DOUBLE NOT NULL,
    battery_health DOUBLE NOT NULL,
    region_code VARCHAR(2) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_region (region_code),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- 预警规则表
CREATE TABLE IF NOT EXISTS warning_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '序号',
    rule_number INT NOT NULL COMMENT '规则编号',
    name VARCHAR(50) NOT NULL COMMENT '规则名称',
    battery_type VARCHAR(20) NOT NULL COMMENT '电池类型',
    warning_rule TEXT NOT NULL COMMENT '预警规则',
    UNIQUE KEY uk_rule_battery (rule_number, battery_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- 电池信号表
CREATE TABLE IF NOT EXISTS battery_signal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vid VARCHAR(16) NOT NULL,
    max_voltage DOUBLE NOT NULL COMMENT '最高电压(Mx)',
    min_voltage DOUBLE NOT NULL COMMENT '最低电压(Mi)',
    max_current DOUBLE NOT NULL COMMENT '最高电流(Ix)',
    min_current DOUBLE NOT NULL COMMENT '最低电流(Ii)',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_vid_time (vid, create_time),
    CONSTRAINT fk_battery_signal_vehicle FOREIGN KEY (vid) REFERENCES vehicle (vid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电池信号表';

-- 预警记录表
CREATE TABLE IF NOT EXISTS warning_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vid VARCHAR(16) NOT NULL,
    rule_number INT NOT NULL,
    warning_level INT NOT NULL COMMENT '预警等级',
    warning_message VARCHAR(255) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_vid_time (vid, create_time),
    CONSTRAINT fk_warning_record_vehicle FOREIGN KEY (vid) REFERENCES vehicle (vid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- 序列号管理表
CREATE TABLE IF NOT EXISTS sequence_manager (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    region_code VARCHAR(2) NOT NULL,
    batch_number VARCHAR(4) NOT NULL,
    current_sequence BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_region_batch (region_code, batch_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='序列号管理表';

-- 插入预警规则数据
INSERT INTO warning_rule (rule_number, name, battery_type, warning_rule) VALUES
(1, '电压差报警', 'TERNARY', 
'{
    "voltageDiff": {
        "0": {"min": 5.0},
        "1": {"min": 3.0, "max": 5.0},
        "2": {"min": 1.0, "max": 3.0},
        "3": {"min": 0.6, "max": 1.0},
        "4": {"min": 0.2, "max": 0.6},
        "none": {"max": 0.2}
    }
}'),
(1, '电压差报警', 'LITHIUM_IRON', 
'{
    "voltageDiff": {
        "0": {"min": 2.0},
        "1": {"min": 1.0, "max": 2.0},
        "2": {"min": 0.7, "max": 1.0},
        "3": {"min": 0.4, "max": 0.7},
        "4": {"min": 0.2, "max": 0.4},
        "none": {"max": 0.2}
    }
}'),
(2, '电流差报警', 'TERNARY', 
'{
    "currentDiff": {
        "0": {"min": 3.0},
        "1": {"min": 1.0, "max": 3.0},
        "2": {"min": 0.2, "max": 1.0},
        "none": {"max": 0.2}
    }
}'),
(2, '电流差报警', 'LITHIUM_IRON', 
'{
    "currentDiff": {
        "0": {"min": 1.0},
        "1": {"min": 0.5, "max": 1.0},
        "2": {"min": 0.2, "max": 0.5},
        "none": {"max": 0.2}
    }
}'); 