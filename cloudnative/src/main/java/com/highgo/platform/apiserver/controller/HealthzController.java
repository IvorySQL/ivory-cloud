package com.highgo.platform.apiserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthzController {

    @Value("${common.serviceName: ivory}")
    private String serviceName;

    @RequestMapping(value = "/healthz", method = {RequestMethod.GET})
    String home() {
        return String.format("Hello, %s Service Manager!", serviceName);
    }
}
