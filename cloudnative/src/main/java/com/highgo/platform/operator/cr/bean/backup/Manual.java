package com.highgo.platform.operator.cr.bean.backup;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/5
 */
@Data
public class Manual {
    /**
     *  仓库name
     */
    private String repoName = "repo1";

    private List<String> options = new ArrayList<>(Arrays.asList("--type=incr","--start-fast=y"));
}
