package com.highgo.platform.operator.cr.bean.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/1/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestoreCluster {

    private String clusterName;

    private String clusterNamespace;

    @Builder.Default
    private String repoName="repo1";

    @Builder.Default
    private List<String> options = new ArrayList<>(Arrays.asList("--type=time"));

}
