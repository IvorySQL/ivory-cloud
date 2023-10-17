package com.highgo.platform.operator.cr.bean.status;

import lombok.Data;

@Data
public class Repo {

    private boolean bound;

    private String name;

    private boolean replicaCreateBackupComplete;

    private String repoOptionsHash;

    private boolean stanzaCreated;

    private String volume;
}
