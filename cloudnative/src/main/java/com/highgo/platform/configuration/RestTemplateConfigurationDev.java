package com.highgo.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfigurationDev {


    @Bean("dbRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
