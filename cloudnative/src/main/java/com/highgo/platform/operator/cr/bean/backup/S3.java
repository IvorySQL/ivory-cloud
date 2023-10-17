package com.highgo.platform.operator.cr.bean.backup;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class S3 {

    private String bucket;

    private String endpoint;

    private String region;
}
