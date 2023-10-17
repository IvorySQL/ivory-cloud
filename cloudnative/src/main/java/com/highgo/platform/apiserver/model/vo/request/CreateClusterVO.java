package com.highgo.platform.apiserver.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author lucunqiao
 * @date 2023/2/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClusterVO implements Serializable {

    /**
     * 创建 k8s 集群信息
     */
    @NotBlank
    private String serverUrl; //集群ip

    private String clusterName; // 集群名称

    private String serverUser; // ssh  user

    private String serverPass; // ssh pass

    private Integer serverSshport; // ssh port

    private String configPath; //config文件路径

    private String clusterId; //集群id

}
