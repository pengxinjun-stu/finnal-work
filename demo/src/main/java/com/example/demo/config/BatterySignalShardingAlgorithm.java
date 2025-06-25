package com.example.demo.config;

import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

public class BatterySignalShardingAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {
    private Properties props = new Properties();

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Comparable<?>> shardingValue) {
        Collection<String> result = new LinkedHashSet<>();
        Map<String, Collection<Comparable<?>>> columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        
        // 获取分片键的值
        Collection<Comparable<?>> createTimes = columnNameAndShardingValuesMap.get("create_time");
        Collection<Comparable<?>> vids = columnNameAndShardingValuesMap.get("vid");
        
        // 如果没有时间或VID，返回所有可用表
        if (createTimes == null || createTimes.isEmpty() || vids == null || vids.isEmpty()) {
            return availableTargetNames;
        }

        for (Comparable<?> createTimeObj : createTimes) {
            LocalDateTime createTime = (LocalDateTime) createTimeObj;
            String yearMonth = createTime.format(DateTimeFormatter.ofPattern("yyyyMM"));
            
            for (Comparable<?> vidObj : vids) {
                String vid = vidObj.toString();
                // 使用VID的哈希值来确定分片
                int shardingKey = Math.abs(vid.hashCode() % 2);
                
                // 构建实际的表名
                String tableName = String.format("battery_signal_%s_%d", yearMonth, shardingKey);
                
                // 如果该表在可用表列表中，添加到结果集
                for (String availableTargetName : availableTargetNames) {
                    if (availableTargetName.endsWith(tableName)) {
                        result.add(availableTargetName);
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }

    @Override
    public String getType() {
        return "BATTERY_SIGNAL_COMPLEX";
    }
} 