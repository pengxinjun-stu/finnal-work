package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {
    public static final String TOPIC_NAME = "battery-warning-topic";
    public static final String PRODUCER_GROUP = "battery-warning-group";
    public static final String CONSUMER_GROUP = "battery-warning-consumer-group";
} 