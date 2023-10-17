package com.highgo.platform.operator.cr.bean.patroni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lucunqiao
 * @date 2023/1/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicConfiguration {
    private Postgresql postgresql;
}
