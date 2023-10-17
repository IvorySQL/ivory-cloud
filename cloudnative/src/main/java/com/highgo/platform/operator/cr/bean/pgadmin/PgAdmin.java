package com.highgo.platform.operator.cr.bean.pgadmin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highgo.platform.operator.cr.bean.common.DataVolumeClaimSpec;
import com.highgo.platform.operator.cr.bean.service.DatabaseService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2022/12/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgAdmin {
    /**
     * 镜像
     */
    private String image;

    /**
     * 副本数
     */
    @Builder.Default
    private int replicas = 1;

    /**
     * service
     */
    private DatabaseService service;

    /**
     * 磁盘空间
     */
    private DataVolumeClaimSpec dataVolumeClaimSpec;

}
