package com.movile.chatclub.webinar.processor.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.max-clients}")
    private Integer maxClients;

    @Bean
    public JedisPool redisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxClients);
        poolConfig.setJmxEnabled(true);

        return new JedisPool(poolConfig, redisHost, redisPort);
    }

}
