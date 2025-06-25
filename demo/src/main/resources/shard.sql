-- 创建北京区数据库
CREATE DATABASE IF NOT EXISTS vehicle_db_bj DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vehicle_db_bj;

-- 创建上海区数据库
CREATE DATABASE IF NOT EXISTS vehicle_db_sh DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vehicle_db_sh;

-- 创建广州区数据库
CREATE DATABASE IF NOT EXISTS vehicle_db_gz DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vehicle_db_gz;

-- 在每个数据库中创建相同的表结构
-- 以下SQL需要在每个数据库中执行

-- 车辆信息表（按地区分库）
CREATE TABLE IF NOT EXISTS vehicle (
    vid VARCHAR(16) NOT NULL COMMENT '车辆识别码',
    frame_number VARCHAR(255) NOT NULL COMMENT '车架编号',
    battery_type VARCHAR(20) NOT NULL COMMENT '电池类型',
    total_mileage DOUBLE NOT NULL COMMENT '总里程(km)',
    battery_health DOUBLE NOT NULL COMMENT '电池健康状态(%)',
    region_code VARCHAR(2) NOT NULL COMMENT '地区代码',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (vid),
    INDEX idx_region_code (region_code),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- 序列管理表（按地区分库）
CREATE TABLE IF NOT EXISTS sequence_manager (
    id BIGINT NOT NULL AUTO_INCREMENT,
    region_code VARCHAR(2) NOT NULL COMMENT '地区代码',
    batch_number VARCHAR(4) NOT NULL COMMENT '批次号',
    current_sequence BIGINT NOT NULL COMMENT '当前序列号',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_region_batch (region_code, batch_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='序列管理表';

-- 预警规则表（每个库保存完整副本）
CREATE TABLE IF NOT EXISTS warning_rule (
    id BIGINT NOT NULL AUTO_INCREMENT,
    battery_type VARCHAR(20) NOT NULL COMMENT '电池类型',
    rule_number INT NOT NULL COMMENT '规则编号',
    warning_rule TEXT NOT NULL COMMENT '预警规则(JSON)',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_battery_type_rule (battery_type, rule_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- 预警记录表（按地区分库）
CREATE TABLE IF NOT EXISTS warning_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    vid VARCHAR(16) NOT NULL COMMENT '车辆识别码',
    rule_number INT NOT NULL COMMENT '规则编号',
    warning_level INT NOT NULL COMMENT '预警等级',
    warning_message VARCHAR(255) NOT NULL COMMENT '预警信息',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_vid (vid),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- 电池信号表（按月分表 + 地区分库）
-- 创建当前月份的表
SET @current_month = DATE_FORMAT(CURRENT_DATE, '%Y%m');
SET @create_signal_table = CONCAT('
CREATE TABLE IF NOT EXISTS battery_signal_', @current_month, '_0 (
    id BIGINT NOT NULL AUTO_INCREMENT,
    vid VARCHAR(16) NOT NULL COMMENT "车辆识别码",
    max_voltage DOUBLE NOT NULL COMMENT "最高电压",
    min_voltage DOUBLE NOT NULL COMMENT "最低电压",
    max_current DOUBLE NOT NULL COMMENT "最高电流",
    min_current DOUBLE NOT NULL COMMENT "最低电流",
    create_time DATETIME NOT NULL COMMENT "创建时间",
    PRIMARY KEY (id),
    INDEX idx_vid (vid),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="电池信号表"');

PREPARE stmt FROM @create_signal_table;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建下一个月的表
SET @next_month = DATE_FORMAT(DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH), '%Y%m');
SET @create_next_signal_table = CONCAT('
CREATE TABLE IF NOT EXISTS battery_signal_', @next_month, '_0 (
    id BIGINT NOT NULL AUTO_INCREMENT,
    vid VARCHAR(16) NOT NULL COMMENT "车辆识别码",
    max_voltage DOUBLE NOT NULL COMMENT "最高电压",
    min_voltage DOUBLE NOT NULL COMMENT "最低电压",
    max_current DOUBLE NOT NULL COMMENT "最高电流",
    min_current DOUBLE NOT NULL COMMENT "最低电流",
    create_time DATETIME NOT NULL COMMENT "创建时间",
    PRIMARY KEY (id),
    INDEX idx_vid (vid),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="电池信号表"');

PREPARE stmt FROM @create_next_signal_table;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建分片1的表
SET @create_signal_table_1 = CONCAT('
CREATE TABLE IF NOT EXISTS battery_signal_', @current_month, '_1 LIKE battery_signal_', @current_month, '_0');
PREPARE stmt FROM @create_signal_table_1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @create_next_signal_table_1 = CONCAT('
CREATE TABLE IF NOT EXISTS battery_signal_', @next_month, '_1 LIKE battery_signal_', @next_month, '_0');
PREPARE stmt FROM @create_next_signal_table_1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 