package com.highgo.platform.operator.cr.bean.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestoreDatasource {

    private RestoreCluster postgresCluster;
}

