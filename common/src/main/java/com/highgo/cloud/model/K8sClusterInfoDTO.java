package com.highgo.cloud.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class K8sClusterInfoDTO implements Serializable {

    private String clusterId;
    private String clusterName;
    private String serverUrl;
    private String config;

    private String serverUser; // ssh  user

    private String serverPass; // ssh pass

    private Integer serverSshport; // ssh port

    private String configPath; //config文件路径

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted = false;

}
