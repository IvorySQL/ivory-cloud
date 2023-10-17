package com.highgo.platform.operator.cr.bean.backup;

import lombok.Data;

@Data
public class Repo {

    String name = "repo1";

    private Volume volume;

    private S3 s3;

}
