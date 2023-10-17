package com.highgo.platform.operator.cr.bean.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolumeClaimSpec {
    private List<String> accessModes = new ArrayList<>(Arrays.asList("ReadWriteOnce"));

    private StorageResource resources;

    private String storageClassName;


}
