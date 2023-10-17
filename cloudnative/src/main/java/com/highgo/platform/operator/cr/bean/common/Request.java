package com.highgo.platform.operator.cr.bean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Builder.Default
    private String cpu = "500m";
    @Builder.Default
    private String memory = "500Mi";

}
