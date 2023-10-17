package com.highgo.platform.operator.cr.bean.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseService {

    /**
     *  svc类型 NodePort ClusterIP LoadBalancer
     */
    private String type;
}
