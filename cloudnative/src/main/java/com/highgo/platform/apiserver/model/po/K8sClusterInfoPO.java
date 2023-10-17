package com.highgo.platform.apiserver.model.po;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description k8s 集群信息表
 * @author srk
 * @date 2023/9/21 10:08
 */

@Entity
@Data
@Table(name = "k8s_cluster_info")
public class K8sClusterInfoPO extends BaseEntity {
    /**
     *  集群id
     */
    private String clusterId;
    /**
     *  集群ip
     */
    private String serverUrl;
    /**
     *  集群配置信息
     */
    private String config; 
    /**
     *  集群名称
     */
    private String clusterName; 
    /**
     *  ssh  user
     */
    private String serverUser; 
    /**
     *  ssh pass
     */
    private String serverPass; 
    /**
     *  ssh port
     */
    private int serverSshport; 
    /**
     *  config文件路径
     */
    private String configPath;
}
