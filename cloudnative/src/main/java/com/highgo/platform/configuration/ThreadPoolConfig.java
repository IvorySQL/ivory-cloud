package com.highgo.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池管理配置
 * @author renlizou
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("monitorExecutor")
    public Executor monitorExecutor(){
        ThreadPoolTaskExecutor monitorExecutor = new ThreadPoolTaskExecutor();
        monitorExecutor.setCorePoolSize(10);
        monitorExecutor.setMaxPoolSize(20);
        monitorExecutor.setQueueCapacity(100);
        monitorExecutor.setKeepAliveSeconds(60);
        monitorExecutor.setThreadNamePrefix("monitorExecutor");
        monitorExecutor.setWaitForTasksToCompleteOnShutdown(true);
        monitorExecutor.setAwaitTerminationSeconds(60);
        monitorExecutor.initialize();
        return monitorExecutor;
    }

    @Bean("autoScalingExecutor")
    public Executor autoScalingExecutor(){
        ThreadPoolTaskExecutor autoScalingExecutor = new ThreadPoolTaskExecutor();
        autoScalingExecutor.setCorePoolSize(10);
        autoScalingExecutor.setMaxPoolSize(20);
        autoScalingExecutor.setQueueCapacity(100);
        autoScalingExecutor.setKeepAliveSeconds(60);
        autoScalingExecutor.setThreadNamePrefix("autoScalingExecutor");
        autoScalingExecutor.setWaitForTasksToCompleteOnShutdown(true);
        autoScalingExecutor.setAwaitTerminationSeconds(60);
        autoScalingExecutor.initialize();
        return autoScalingExecutor;
    }

}
