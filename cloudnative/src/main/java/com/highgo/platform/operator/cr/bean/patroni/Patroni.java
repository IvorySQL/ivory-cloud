package com.highgo.platform.operator.cr.bean.patroni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patroni {
    private DynamicConfiguration dynamicConfiguration;

    @Builder.Default
    private int leaderLeaseDurationSeconds = 30;

    @Builder.Default
    private int port = 8008;

    @Builder.Default
    private int syncPeriodSeconds = 10;

}


