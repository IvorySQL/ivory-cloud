package com.highgo.platform.apiserver.model.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lucunqiao
 * @date 2023/2/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterInfoVO {
    /**
     * k8s 集群信息表
     */

    private String clusterId; // 集群id

    private String serverUrl; //集群ip

    private String serverUser; //集群user

    private String serverPass; //集群password

    private String serverSshport; //集群ssh port

    private String config; // 集群配置信息

    private String clusterName; // 集群名称

    private Date createdAt; // 集群创建时间

    private Date updatedAt; // 集群更新时间

    private String configPath; //config文件路径

}
