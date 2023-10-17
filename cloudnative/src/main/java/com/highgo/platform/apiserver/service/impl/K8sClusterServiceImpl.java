package com.highgo.platform.apiserver.service.impl;

import com.highgo.cloud.util.SshUtil;
import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.cloud.model.K8sClusterInfoDTO;
import com.highgo.platform.apiserver.model.po.InstancePO;
import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.model.vo.request.CreateClusterVO;
import com.highgo.cloud.model.ServerConnectVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ClusterInfoVO;
import com.highgo.platform.apiserver.model.vo.response.K8sResourceCountVO;
import com.highgo.platform.apiserver.repository.InstanceRepository;
import com.highgo.platform.apiserver.repository.K8sClusterInfoRepository;
import com.highgo.platform.apiserver.service.K8sClusterService;
import com.highgo.platform.configuration.K8sClientConfiguration;
import com.highgo.cloud.enums.InstanceStatus;
import com.highgo.cloud.enums.InstanceType;
import com.highgo.platform.errorcode.ClusterError;
import com.highgo.platform.exception.ClusterException;
import com.highgo.cloud.util.BeanUtil;
import com.highgo.cloud.util.CommonUtil;
import com.jcraft.jsch.JSchException;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("k8sClusterService")
public class K8sClusterServiceImpl implements K8sClusterService {

    private static final Logger logger = LoggerFactory.getLogger(K8sClusterServiceImpl.class);

    @Autowired
    private K8sClusterInfoRepository k8sClusterInfoRepository;

    @Value("${common.linuxk8sConfigPath}")
    private String configPath;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    K8sClientConfiguration k8sClientConfiguration;

    //k8s 默认的命名空间
    private final List<String> kubeNamespaces = new ArrayList<>(Arrays.asList("kube-node-lease","kube-flannel","kube-public","kube-system"));

    private List<String> processingIps = new ArrayList<>();

    /**
     * 通过集群id查询集群配置信息
     *
     * @param clusterId 集群ID
     * @return 集群配置信息
     */
    @Override
    public String getClusterConfigById(String clusterId) {
        Optional<K8sClusterInfoPO> k8sClusterInfoPOOptional = k8sClusterInfoRepository.findById(clusterId);
        if (k8sClusterInfoPOOptional.isPresent()) {
            return k8sClusterInfoPOOptional.get().getConfig();
        } else {
            return null;
        }
    }

    @Override
    public void saveClusterInfo(String clusterId) {

    }

    ///**
    // * 通过集群id，查询集群配置信息并入库
    // *
    // * @param clusterId
    // */
    //@Override
    //public void saveClusterInfo(String clusterId) {
    //    Optional<K8sClusterInfoPO> clusterConfigOptional = k8sClusterInfoRepository.findByClusterId(clusterId);
    //    if(!clusterConfigOptional.isPresent()){
    //        K8sClusterInfoDTO k8sClusterInfoDTO = hcpService.getClusterInfoByClusterId(clusterId);
    //        k8sClusterInfoDTO.setConfig(hcpService.getClusterConfig(clusterId));
    //        K8sClusterInfoPO k8sClusterInfoPO = new K8sClusterInfoPO();
    //        k8sClusterInfoPO.setCreatedAt(CommonUtils.getUTCDate());
    //        BeanUtils.copyNotNullProperties(k8sClusterInfoDTO, k8sClusterInfoPO);
    //        saveK8sCluster(k8sClusterInfoPO);
    //        logger.info("K8sClusterServiceImpl.saveClusterConfigById. save cluster config success. clusterId:{}",clusterId);
    //    }
    //}

    /**
     * 查询已纳管的集群列表
     *
     * @return 集群列表
     */
    @Override
    public List<K8sClusterInfoPO> getK8sClusterList() {
        return k8sClusterInfoRepository.listCluster();
    }

    /**
     * 保存k8s集群信息
     *
     * @param k8sClusterInfoPO 集群信息PO
     */
    @Override
    public void saveK8sCluster(K8sClusterInfoPO k8sClusterInfoPO) {
        k8sClusterInfoRepository.save(k8sClusterInfoPO);
    }

    @Override
    public void saveAllK8sCluster() {

    }

    ///**
    // * 从CNP平台查询纳管的集群保存入库，并删除未纳管的集群
    // */
    //@Override
    //public void saveAllK8sCluster() {
    //    List<String> clusterIdListSaved = k8sClusterInfoRepository.listClusterId();
    //    List<String> clusterIdListForCNP = hcpService.listClusterId();
    //    logger.info("[K8sClusterServiceImpl.saveAllK8sCluster] clusters in db is {}", Arrays.toString(clusterIdListSaved.toArray()));
    //    logger.info("[K8sClusterServiceImpl.saveAllK8sCluster] clusters from hcp is {}", Arrays.toString(clusterIdListForCNP.toArray()));
    //    // 保存新增加的集群 clusterIdListForCNP - clusterIdListSaved
    //    List<String> subtrClusterIdListForCNP = clusterIdListForCNP.stream().filter(clusterId -> !clusterIdListSaved.contains(clusterId)).collect(Collectors.toList());
    //    for(String clusterId: subtrClusterIdListForCNP){
    //        try{
    //            saveClusterInfo(clusterId);
    //        }catch (Exception e){
    //            logger.error("[K8sClusterServiceImpl.saveAllK8sCluster] save cluster info error! cluster id is {}", clusterId);
    //            logger.error("[K8sClusterServiceImpl.saveAllK8sCluster] save cluster info error.", e);
    //        }
    //    }
    //    // 删除移除的集群 clusterIdListSaved - clusterIdListForCNP
    //    List<String> subtrClusterIdListSaved = clusterIdListSaved.stream().filter(clusterId -> !clusterIdListForCNP.contains(clusterId)).collect(Collectors.toList());
    //    Date date = CommonUtils.getUTCDate();
    //    for(String clusterId: subtrClusterIdListSaved){
    //        k8sClusterInfoRepository.deleteByClusterId(clusterId, date);
    //        logger.info("[K8sClusterServiceImpl.saveAllK8sCluster] delete cluster success. {}", clusterId);
    //    }
    //}

    @Override
    public K8sClusterInfoPO getInfoByClusterId(String clusterId) {
        Optional<K8sClusterInfoPO> clusterConfigOptional = k8sClusterInfoRepository.findByClusterId(clusterId);
        if(!clusterConfigOptional.isPresent()){
            logger.error("[K8sClusterServiceImpl.getInfoByClusterId] cluster info is not exist! clusterId is {}", clusterId);
            return null;
        }
        return clusterConfigOptional.get();
    }

    /**
     * 查询已纳管的集群信息
     *
     * @return 集群Map
     */
    @Override
    public Map<String, K8sClusterInfoPO> getK8sClusterMap() {
        Map<String, K8sClusterInfoPO> k8sClusterInfoPOMap = new HashMap<>();
        for(K8sClusterInfoPO k8sClusterInfoPO:getK8sClusterList()){
            k8sClusterInfoPOMap.put(k8sClusterInfoPO.getClusterId(), k8sClusterInfoPO);
        }
        return k8sClusterInfoPOMap;
    }

    @Override
    public List<ClusterInfoVO> list() {
        List<ClusterInfoVO> clusterInfoVOs = new ArrayList<>();

        for (K8sClusterInfoPO k8sClusterInfoPO : k8sClusterInfoRepository.listCluster()) {
            ClusterInfoVO clusterInfoVO = new ClusterInfoVO();
            BeanUtil.copyNotNullProperties(k8sClusterInfoPO, clusterInfoVO);
            clusterInfoVOs.add(clusterInfoVO);
        }
        return clusterInfoVOs;
    }

    @Override
    public ClusterInfoVO insertCluster(CreateClusterVO createClusterVO) {
        logger.info("The list of processing ip: {}", Arrays.toString(processingIps.toArray()));
        if (processingIps.contains(createClusterVO.getServerUrl())) {
            logger.error("The master node ip {} address is in process.", createClusterVO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_MASTER_IP_IN_PROCESS);
        }else{
            processingIps.add(createClusterVO.getServerUrl());
        }

        K8sClusterInfoDTO k8sClusterInfoDTO = new K8sClusterInfoDTO();
        BeanUtil.copyNotNullProperties(createClusterVO,k8sClusterInfoDTO);

        if(StringUtils.isEmpty(k8sClusterInfoDTO.getConfigPath())){
            k8sClusterInfoDTO.setConfigPath(configPath);
        }

        List<String> serverUrls = k8sClusterInfoRepository
                .listCluster()
                .stream()
                .map(K8sClusterInfoPO::getServerUrl)
                .collect(Collectors.toList());

        if(serverUrls.contains(k8sClusterInfoDTO.getServerUrl())){
            processingIps.remove(k8sClusterInfoDTO.getServerUrl());
            logger.error("The master node ip {} address already exists.", k8sClusterInfoDTO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_MASTER_IP_CONFLICT_ERROR);
        }

        try {
            String result = getConfigString(k8sClusterInfoDTO);

            k8sClusterInfoDTO.setConfig(result);
            Date now = CommonUtil.getUTCDate();
            k8sClusterInfoDTO.setCreatedAt(now);
            k8sClusterInfoDTO.setClusterId(CommonUtil.uuid());

        } catch (Exception e) {
            processingIps.remove(k8sClusterInfoDTO.getServerUrl());
            logger.error("Failed to obtain the cluster config file.The cluster master ip is: {}",k8sClusterInfoDTO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_CONFIG_ERROR);
        }

        K8sClusterInfoPO k8sClusterInfoPO = new K8sClusterInfoPO();
        BeanUtil.copyNotNullProperties(k8sClusterInfoDTO,k8sClusterInfoPO);
        k8sClusterInfoRepository.save(k8sClusterInfoPO);

        ClusterInfoVO clusterInfoVO = new ClusterInfoVO();
        BeanUtil.copyNotNullProperties(k8sClusterInfoPO,clusterInfoVO);
        processingIps.remove(k8sClusterInfoDTO.getServerUrl());
        return clusterInfoVO;
    }

    @Override
    public ActionResponse delCluster(String clusterId) {

        List<InstancePO> instancePOList = instanceRepository.listByClusterId(clusterId);
        if(!CollectionUtils.isEmpty(instancePOList)){
            logger.error("The cluster still has instances and cannot be deleted.The cluster id is: {}",clusterId);
            throw new ClusterException(ClusterError.CLUSTER_NOT_ALLOW_ERROR);
        }

        Date now = CommonUtil.getUTCDate();
        k8sClusterInfoRepository.deleteByClusterId(clusterId,now);
        return ActionResponse.actionSuccess();
    }

    @Override
    public ClusterInfoVO updateCluster(CreateClusterVO createClusterVO) {
        K8sClusterInfoDTO k8sClusterInfoDTO = new K8sClusterInfoDTO();
        BeanUtil.copyNotNullProperties(createClusterVO,k8sClusterInfoDTO);

        if(StringUtils.isEmpty(k8sClusterInfoDTO.getConfigPath())){
            k8sClusterInfoDTO.setConfigPath(configPath);
        }
        Optional<K8sClusterInfoPO> cluster = k8sClusterInfoRepository.findByClusterId(createClusterVO.getClusterId());
        if(!cluster.isPresent()){
            logger.error("[K8sClusterServiceImpl.updateCluster] cluster is not exits. clusterId is {}", createClusterVO.getClusterId());
            throw new ClusterException(ClusterError.CLUSTER_NOT_EXIST_ERROR);
        }

        List<InstancePO> instancePOList = instanceRepository.listByClusterId(createClusterVO.getClusterId());
        if(!CollectionUtils.isEmpty(instancePOList)){
            logger.error("The cluster still has instances and cannot be updated.The cluster id is: {}",createClusterVO.getClusterId());
            throw new ClusterException(ClusterError.CLUSTER_NOT_ALLOW_ERROR);
        }

        try {
            String result = getConfigString(k8sClusterInfoDTO);

            k8sClusterInfoDTO.setConfig(result);
            Date now = CommonUtil.getUTCDate();
            k8sClusterInfoDTO.setUpdatedAt(now);

        } catch (Exception e) {
            logger.error("Failed to obtain the cluster config file.The cluster master ip is: {}",k8sClusterInfoDTO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_CONFIG_ERROR);
        }

        K8sClusterInfoPO k8sClusterInfoPO = cluster.get();
        BeanUtil.copyNotNullProperties(k8sClusterInfoDTO,k8sClusterInfoPO);
        K8sClusterInfoPO save = k8sClusterInfoRepository.save(k8sClusterInfoPO);
        ClusterInfoVO clusterInfoVO = new ClusterInfoVO();
        BeanUtil.copyNotNullProperties(save,clusterInfoVO);

        return clusterInfoVO;
    }

    /**
     * description: 获取k8s集群config文件string
     * date: 2023/3/13 15:12
     * @param k8sClusterInfoDTO
     * @return: String
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private String getConfigString(K8sClusterInfoDTO k8sClusterInfoDTO) throws JSchException, IOException {
        ServerConnectVO server = ServerConnectVO
                .builder()
                .host(k8sClusterInfoDTO.getServerUrl())
                .user(k8sClusterInfoDTO.getServerUser())
                .password(k8sClusterInfoDTO.getServerPass())
                .port(k8sClusterInfoDTO.getServerSshport())
                .command("cat " + k8sClusterInfoDTO.getConfigPath())
                .build();
        String result = SshUtil.remoteExeCommand(server);

        if (StringUtils.isEmpty(result)) {
            logger.error("The configuration file for the master node {} is invalid.", k8sClusterInfoDTO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_CONFIG_INVALID);
        }

        try {
            //校验config
            KubeConfigUtils.parseConfigFromString(result);
        } catch (Exception e) {
            logger.error("The configuration file for the master node {} is invalid.", k8sClusterInfoDTO.getServerUrl());
            throw new ClusterException(ClusterError.CLUSTER_CONFIG_INVALID);
        }
        return result;
    }

    @Override
    public List<Namespace> getNamespace(String clusterId) {
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        return kubernetesClient
                .namespaces()
                .list()
                .getItems()
                .stream()
                .filter(n -> !n.getMetadata().getName().startsWith("kube-"))
                .filter(n -> !kubeNamespaces.contains(n.getMetadata().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<K8sResourceCountVO> countResource(String userId) {
        List<String> k8sclusterIds = k8sClusterInfoRepository.listClusterId();
        //instance  按照clusterid分组
        Map<String, List<InstanceDTO>> listInstanceMap = k8sclusterIds
                .stream()
                .map((k) -> {
                    if("0".equals(userId)){
                        //admin用户
                        return instanceRepository.listByClusterId(k);
                    }else{
                        //其他用户
                        return instanceRepository.listByClusterIdUserId(k,userId);
                    }
                })
                .flatMap(Collection::stream)
                .map(i -> {
                    InstanceDTO instanceDTO = new InstanceDTO();
                    BeanUtil.copyNotNullProperties(i,instanceDTO);
                    return instanceDTO;
                })
                .collect(Collectors.groupingBy(InstanceDTO::getClusterId));

        List<K8sResourceCountVO> k8sResourceCountVOList = new ArrayList<>();

        listInstanceMap.forEach((k,v)->{

            String clusterName = k8sClusterInfoRepository.findByClusterId(k).get().getClusterName();

            //运行中
            long runningCount = v
                    .stream()
                    .filter(i -> InstanceStatus.RUNNING.name().equalsIgnoreCase(i.getStatus().name()))
                    .count();
            //错误
            long errorCount = v
                    .stream()
                    .filter(i -> InstanceStatus.ERROR.name().equalsIgnoreCase(i.getStatus().name()))
                    .count();
            //其他
            long elseCount = v
                    .stream()
                    .filter(i -> !InstanceStatus.RUNNING.name().equalsIgnoreCase(i.getStatus().name()))
                    .filter(i -> !InstanceStatus.ERROR.name().equalsIgnoreCase(i.getStatus().name()))
                    .count();
            //单机
            long aloneCount = v
                    .stream()
                    .filter(i -> InstanceStatus.RUNNING.name().equalsIgnoreCase(i.getStatus().name()))
                    .filter(i -> i.getType().name().equalsIgnoreCase(InstanceType.ALONE.name()))
                    .count();
            //高可用
            long haCount = v
                    .stream()
                    .filter(i -> InstanceStatus.RUNNING.name().equalsIgnoreCase(i.getStatus().name()))
                    .filter(i -> i.getType().name().equalsIgnoreCase(InstanceType.HA.name()))
                    .count();


            K8sResourceCountVO k8sResourceCountVO = K8sResourceCountVO
                    .builder()
                    .clusterId(k)
                    .clusterName(clusterName)
                    .instanceCount((long) v.size())
                    .aloneInstanceCount(aloneCount)
                    .haInstanceCount(haCount)
                    .elseInstanceCount(elseCount)
                    .errorInstanceCount(errorCount)
                    .runningInstanceCount(runningCount)
                    .build();
            k8sResourceCountVOList.add(k8sResourceCountVO);
        });

        //加上没有资源的k8s环境
        k8sclusterIds.removeAll(listInstanceMap.keySet());
        for(String id : k8sclusterIds){
            String clusterName = k8sClusterInfoRepository.findByClusterId(id).get().getClusterName();
            K8sResourceCountVO k8sResourceCountVO = K8sResourceCountVO
                    .builder()
                    .clusterId(id)
                    .clusterName(clusterName)
                    .instanceCount(0L)
                    .aloneInstanceCount(0L)
                    .haInstanceCount(0L)
                    .elseInstanceCount(0L)
                    .errorInstanceCount(0L)
                    .runningInstanceCount(0L)
                    .build();
            k8sResourceCountVOList.add(k8sResourceCountVO);
        }

        return k8sResourceCountVOList;
    }

    @Override
    public void createNamespace(String clusterId, String namespaceName) {
        //创建namespace
        KubernetesClient kubernetesClient = k8sClientConfiguration.getAdminKubernetesClientById(clusterId);
        Namespace namespace = new NamespaceBuilder()
                .withNewMetadata()
                .withName(namespaceName)
                .endMetadata()
                .build();
        kubernetesClient.namespaces().createOrReplace(namespace);
    }


}
