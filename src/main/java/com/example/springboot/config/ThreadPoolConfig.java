package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "orderFormThreadPool")
    public ThreadPoolExecutor orderFormThreadPool(){
        return new ThreadPoolExecutor(20,
                100,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(40),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
