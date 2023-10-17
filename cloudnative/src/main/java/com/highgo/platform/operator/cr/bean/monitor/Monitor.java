package com.highgo.platform.operator.cr.bean.monitor;

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
public class Monitor {
    /**
     * 监控
     */
    private PgMonitor pgmonitor;
}
