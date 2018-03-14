package com.movile.chatclub.webinar.processor.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public ExecutorService threadPool(ApplicationContext context) {
        int poolSize = 1;

        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            threadPool.submit(context.getBean("processor", Runnable.class));
        }

        return threadPool;
    }

}
