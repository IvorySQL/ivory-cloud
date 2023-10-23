/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.platform.operator.service.impl;

import com.highgo.cloud.constant.DBConstant;
import com.highgo.cloud.constant.OperatorConstant;
import com.highgo.cloud.enums.*;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.vo.response.ConfigParamInfoVO;
import com.highgo.platform.apiserver.repository.ConfigDefinationRepository;
import com.highgo.platform.apiserver.service.ExtraMetaService;
import com.highgo.platform.apiserver.service.InstanceService;
import com.highgo.platform.errorcode.InstanceError;
import com.highgo.platform.exception.InstanceException;
import com.highgo.platform.operator.cr.bean.DatabaseClusterSpec;
import com.highgo.platform.operator.cr.bean.backup.Backup;
import com.highgo.platform.operator.cr.bean.common.DataVolumeClaimSpec;
import com.highgo.platform.operator.cr.bean.common.StorageRequests;
import com.highgo.platform.operator.cr.bean.common.StorageResource;
import com.highgo.platform.operator.cr.bean.imagePullsecret.ImagePullSecret;
import com.highgo.platform.operator.cr.bean.instance.Instance;
import com.highgo.platform.operator.cr.bean.monitor.Exporter;
import com.highgo.platform.operator.cr.bean.monitor.Monitor;
import com.highgo.platform.operator.cr.bean.monitor.PgMonitor;
import com.highgo.platform.operator.cr.bean.patroni.DynamicConfiguration;
import com.highgo.platform.operator.cr.bean.patroni.Patroni;
import com.highgo.platform.operator.cr.bean.patroni.Postgresql;
import com.highgo.platform.operator.cr.bean.pgadmin.PgAdmin;
import com.highgo.platform.operator.cr.bean.pgadmin.UserInterface;
import com.highgo.platform.operator.cr.bean.service.DatabaseService;
import com.highgo.platform.operator.cr.bean.user.User;
import com.highgo.platform.operator.service.OperatorBackupsService;
import com.highgo.platform.operator.service.OperatorClusterSpecService;
import com.highgo.platform.operator.service.OperatorInstanceService;
import com.highgo.platform.operator.service.OperatorRestoreService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OperatorClusterSpecServiceImpl implements OperatorClusterSpecService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorClusterSpecServiceImpl.class);

    @Value(value = "${images.image.db}")
    private String databaseImage;

    @Value(value = "${images.image.pgadmin}")
    private String pgadminImage;

    @Value(value = "${images.image.exporter}")
    private String exporterImage;

    @Value(value = "${common.imagePullSecret:service-registry}")
    private String imagePullSecret;

    @Autowired
    private OperatorInstanceService operatorInstanceService;

    @Autowired
    private OperatorBackupsService operatorBackupsService;

    @Autowired
    private OperatorRestoreService operatorRestoreService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private ExtraMetaService extraMetaService;

    @Autowired
    private ConfigDefinationRepository configDefinationRepository;

    /**
     * 构建postgrescluster cr spec
     *
     * @param instanceDTO
     * @return
     */
    @Override
    public DatabaseClusterSpec initClusterSpec(InstanceDTO instanceDTO) {
        int replicas = 1;
        DatabaseClusterSpec databaseClusterSpec = new DatabaseClusterSpec();
        if (IvoryVersion.IVORY23.getKey().equalsIgnoreCase(instanceDTO.getVersion())) {
            databaseClusterSpec.setImage(databaseImage);
            databaseClusterSpec.setPostgresVersion(DBConstant.IVORY_PG_KERNEL_VERSION);
        } else if (HighgoDBVersion.HGDB458.getKey().equalsIgnoreCase(instanceDTO.getVersion())) {
        databaseClusterSpec.setImage(databaseImage);
        databaseClusterSpec.setPostgresVersion(DBConstant.PG_KERNEL_VERSION);
        } else {
            logger.error("[OperatorClusterSpecServiceImpl.initClusterSpec] pg version not support. version is {}",
                    instanceDTO.getVersion());
            throw new InstanceException(InstanceError.INSTANCE_VERSION_NOT_SUPPORT);
        }
        if (InstanceType.HA.equals(instanceDTO.getType())) {
            //todo 改为页面输入
            replicas = 3;
        }
        // 节点数量信息入库 instance_event表
        instanceService.updateNodeNum(instanceDTO.getId(), replicas);
        instanceService.updateNodeReadyNum(instanceDTO.getId(), 0);
        // 管理员密码入库
        extraMetaService.saveExtraMeta(instanceDTO.getId(), OperatorConstant.PASSWORD,
                CommonUtil.base64(instanceDTO.getPassword()));
        // 实例信息
        Instance instance = operatorInstanceService.geInstance(instanceDTO.getName(), replicas, instanceDTO.getCpu(),
                instanceDTO.getMemory(), instanceDTO.getStorage() + "Gi", instanceDTO.getStorageClass());
        databaseClusterSpec.setInstances(new ArrayList<>(Arrays.asList(instance)));
        // 备份信息 storageclass storage
        Backup backup =
                operatorBackupsService.getBackupLocal(instanceDTO.getStorage() + "Gi", instanceDTO.getStorageClass());
        databaseClusterSpec.setBackups(backup);
        // 镜像拉取秘钥
        databaseClusterSpec.setImagePullSecrets(
                new ArrayList<>(Arrays.asList(ImagePullSecret.builder().name(imagePullSecret).build())));
        // svc
        String svcType = OperatorConstant.CLUSTER_IP;
        if (SwitchStatus.ON.equals(instanceDTO.getNodePortSwitch())) {
            svcType = OperatorConstant.NODEPORT;
        }
        databaseClusterSpec.setService(DatabaseService.builder().type(svcType).build());

        // restore 恢复、克隆
        if (StringUtils.isNotEmpty(instanceDTO.getOriginalBackupId())
                && StringUtils.isNotEmpty(instanceDTO.getOriginalInstanceId())) {
            databaseClusterSpec.setDataSource(operatorRestoreService.getDataSourceCluster(instanceDTO));
        }

        // add highgo-lucunqiao
        // 管理员用户
        databaseClusterSpec.setUsers(new ArrayList<>(Arrays.asList(
                User.builder().name(DBConstant.SYSDBA).options(UserOption.SUPERUSER.name()).build(),
                User.builder().name(DBConstant.SYSSAO).options(UserOption.SUPERUSER.name()).build(),
                User.builder().name(DBConstant.SYSSSO).options(UserOption.SUPERUSER.name()).build())));

        // highgoDBClusterSpec.setImagePullPolicy("Always");
        databaseClusterSpec.setImagePullPolicy("IfNotPresent");
        databaseClusterSpec.setPort(DBConstant.SEE_DEFAULT_PORT);

        // 数据库param
        List<Map> maps = configDefinationRepository.listParamByInstanceId(instanceDTO.getId());
        List<ConfigParamInfoVO> configParamInfoVOS = new ArrayList<>();
        for (Map<?, ?> map : maps) {
            ConfigParamInfoVO instanceParamsInfo = new ConfigParamInfoVO();
            BeanUtil.copyProperties(map, instanceParamsInfo);
            configParamInfoVOS.add(instanceParamsInfo);
        }

        Map<String, String> params = configParamInfoVOS
                .stream()
                .collect(Collectors.toMap(ConfigParamInfoVO::getName, ConfigParamInfoVO::getDefaultValue));

        databaseClusterSpec.setPatroni(Patroni
                .builder()
                .dynamicConfiguration(DynamicConfiguration
                        .builder()
                        .postgresql(Postgresql
                                .builder()
                                .parameters(params)
                                .build())
                        .build())
                .build());

        // monitor exporter
        databaseClusterSpec.setMonitoring(Monitor
                .builder()
                .pgmonitor(PgMonitor
                        .builder()
                        .exporter(Exporter
                                .builder()
                                .image(exporterImage)
                                .build())
                        .build())
                .build());

        // pgadmin
        databaseClusterSpec.setUserInterface(UserInterface
                .builder()
                .pgAdmin(PgAdmin
                        .builder()
                        .service(DatabaseService
                                .builder()
                                .type(OperatorConstant.NODEPORT)
                                .build())
                        .dataVolumeClaimSpec(DataVolumeClaimSpec
                                .builder()
                                .resources(StorageResource
                                        .builder()
                                        .requests(StorageRequests
                                                .builder()
                                                .storage("1Gi")
                                                .build())
                                        .build())
                                .storageClassName(instanceDTO.getStorageClass())
                                .build())
                        .image(pgadminImage)
                        .build())
                .build());

        return databaseClusterSpec;
    }
}
