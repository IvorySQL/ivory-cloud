package com.highgo.platform.apiserver.model.dto;


import com.highgo.cloud.enums.NetworkType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Date;

@Data
public class InstanceNetworkDTO implements Serializable {

    /**
     * 网络信息
     */
    private static final long serialVersionUID = -1666060312000L;

    private String instanceId;

    @Enumerated(EnumType.STRING)
    private NetworkType type; // 读写/只读

    private String nodeIp;

    private Integer nodePort;

    private String service; // 内网连接svc信息

    private Integer port; // 内网连接端口

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Boolean isDeleted = false;

}
