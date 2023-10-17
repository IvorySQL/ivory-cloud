package com.highgo.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@ComponentScan("com.highgo")
@ServletComponentScan("com.highgo")
@EnableJpaRepositories({"com.highgo"})
@EntityScan({"com.highgo"})
@EnableScheduling
//@EnableEurekaClient
public class CloudNativeApplication {

    private static final Logger logger = LoggerFactory.getLogger(CloudNativeApplication.class);

    public static void main(String[] args) throws Exception {
        logger.debug("cloud native Service Manager is starting...");
        SpringApplication.run(CloudNativeApplication.class, args);
    }

}
