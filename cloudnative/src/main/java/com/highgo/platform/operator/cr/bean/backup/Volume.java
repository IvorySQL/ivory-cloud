package com.highgo.platform.operator.cr.bean.backup;

import com.highgo.platform.operator.cr.bean.common.VolumeClaimSpec;
import lombok.Data;

@Data
public class Volume {

    private VolumeClaimSpec volumeClaimSpec;
}
