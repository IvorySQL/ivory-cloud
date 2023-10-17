package com.highgo.platform.apiserver.model.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/2/24 13:28
 * @Description: 所有k8s集群资源统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class K8sResourceCountVO {

    private String clusterId;
    private String clusterName;
    private Long aloneInstanceCount;
    private Long haInstanceCount;
    private Long instanceCount;
    private Long errorInstanceCount;
    private Long runningInstanceCount;
    private Long elseInstanceCount;

}
