package com.highgo.platform.operator.cr.bean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2022/12/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataVolumeClaimSpec {
    @Builder.Default
    private List<String> accessModes = new ArrayList<>(Arrays.asList("ReadWriteOnce"));

    private StorageResource resources;

    private String storageClassName;

}
