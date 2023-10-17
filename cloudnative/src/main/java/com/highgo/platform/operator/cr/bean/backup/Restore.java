package com.highgo.platform.operator.cr.bean.backup;

import io.fabric8.kubernetes.api.model.Affinity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Restore {

    /**
     * 亲和性
     */
    private Affinity affinity;

    private boolean enabled = false;

    private String repoName = "repo1";

    private List<String> options = new ArrayList<>(Arrays.asList("--type=time","--force","--delta"));

}
