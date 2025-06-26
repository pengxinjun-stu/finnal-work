# 小米汽车电池管理系统

## 项目概述
本项目是一个基于Spring Boot的汽车电池管理系统，主要用于管理小米汽车的电池信号数据和预警信息。系统支持多区域部署，采用分库分表架构，能够处理大规模的实时数据采集和预警分析。

## 技术架构

### 核心技术栈
- Spring Boot 2.x
- MySQL 8.0
- Redis
- RocketMQ
- ShardingSphere
- HikariCP

### 系统架构
- 分库：基于地理区域（北京、上海、广州、武汉）
- 分表：按时间（月份）+ VID（32分片）
- 缓存：Redis用于热点数据缓存
- 消息队列：RocketMQ用于异步处理预警信息

## 功能特性

### 1. VID生成系统
- 格式：SU7N1{regionCode}{batchNumber}{sequence}
- 示例：SU7N1BJ2403000001
- 组成：品牌(SU7) + 型号(N1) + 地区(BJ) + 年月(2403) + 序号(00001)
- 容量：每地区每月支持99,999辆车
- 年产能：每地区约120万辆

### 2. 电池信号管理
- 每5秒采集一次信号数据
- 支持两种电池类型：
  * 三元锂电池：3.0V-4.2V, 2.0A-2.2A
  * 磷酸铁锂电池：2.5V-3.65V, 1.5A-1.7A
- 95%正常数据，5%异常数据生成
- 使用Redis缓存优化查询性能

### 3. 预警系统
- 电压差异预警规则：
  * 三元锂：Level 0(<0.3V), Level 1(0.3-0.5V), Level 2(≥0.5V)
  * 磷酸铁锂：Level 0(<0.4V), Level 1(0.4-0.6V), Level 2(≥0.6V)
- 电流差异预警规则：
  * 三元锂：Level 0(<0.3A), Level 1(0.3-0.5A), Level 2(≥0.5A)
  * 磷酸铁锂：Level 0(<0.4A), Level 1(0.4-0.6A), Level 2(≥0.6A)

## API接口文档

### 车辆管理接口
```
POST   /api/vehicles            # 创建车辆
GET    /api/vehicles/{vid}     # 获取车辆信息
PUT    /api/vehicles/{vid}     # 更新车辆信息
DELETE /api/vehicles/{vid}     # 删除车辆
GET    /api/vehicles/all       # 分页获取所有车辆
GET    /api/vehicles/region/{regionCode} # 获取区域车辆
```

### 电池信号接口
```
POST   /api/battery/signal           # 创建信号
GET    /api/battery/signal/{id}      # 获取信号
GET    /api/battery/signal/vehicle/{vid} # 获取车辆信号
```

### 预警记录接口
```
GET    /api/warning/vehicle          # 获取车辆预警
GET    /api/warning/history          # 获取预警历史
```

## 部署说明

### 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- RocketMQ 4.9+
- Maven 3.6+

### 数据库配置
1. 创建四个分库：
```sql
CREATE DATABASE xiaomi_bj;
CREATE DATABASE xiaomi_sh;
CREATE DATABASE xiaomi_gz;
CREATE DATABASE xiaomi_wh;
```

2. 执行初始化脚本：
```bash
mysql -u root -p xiaomi_bj < create.sql
mysql -u root -p xiaomi_sh < create.sql
mysql -u root -p xiaomi_gz < create.sql
mysql -u root -p xiaomi_wh < create.sql
```

### 应用配置
1. 修改 `application.properties` 配置：
```properties
# 数据库连接
spring.shardingsphere.datasource.names=ds-bj,ds-sh,ds-gz,ds-wh

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379

# RocketMQ配置
rocketmq.name-server=localhost:9876
rocketmq.producer.group=battery-signal-group
```

2. 编译打包：
```bash
mvn clean package
```

3. 运行应用：
```bash
java -jar demo-0.0.1-SNAPSHOT.jar
```

## 性能指标
- 支持每秒处理1000+信号数据
- 预警检测延迟<100ms
- 查询响应时间<50ms
- 支持横向扩展，可动态增加区域节点

## 监控和运维
- SQL执行监控：开启 `sql-show` 配置
- 应用日志：使用SLF4J+Logback
- 性能指标：集成Spring Boot Actuator
- 数据清理：自动化月度表管理

## 开发团队
- 架构设计：XXX
- 后端开发：XXX
- 测试团队：XXX

## 许可证
MIT License
