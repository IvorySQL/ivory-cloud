package com.highgo.platform.apiserver.service;

import com.highgo.platform.apiserver.model.po.K8sClusterInfoPO;
import com.highgo.platform.apiserver.model.vo.request.CreateClusterVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ClusterInfoVO;
import com.highgo.platform.apiserver.model.vo.response.K8sResourceCountVO;
import io.fabric8.kubernetes.api.model.Namespace;

import java.util.List;
import java.util.Map;

public interface K8sClusterService {

    /**
     * 通过集群id查询集群配置信息(连接集群的配置 .kube/config)
     *
     * @param clusterId 集群ID
     * @return 集群配置信息
     */
    public String getClusterConfigById(String clusterId);

    /**
     * 通过集群id，查询集群信息并入库
     * @param clusterId
     */
    public void saveClusterInfo(String clusterId);

    /**
     * 查询已纳管的集群列表
     *
     * @return 集群列表
     */
    public List<K8sClusterInfoPO> getK8sClusterList();

    /**
     * 保存k8s集群信息
     *
     * @param k8sClusterInfoPO 集群信息PO
     */
    public void saveK8sCluster(K8sClusterInfoPO k8sClusterInfoPO);

    /**
     * 从CNP平台查询纳管的集群保存入库，并删除未纳管的集群
     */
    public void saveAllK8sCluster();

    public K8sClusterInfoPO getInfoByClusterId(String clusterId);

    /**
     * 查询已纳管的集群信息
     *
     * @return 集群Map
     */
    public Map<String, K8sClusterInfoPO> getK8sClusterMap();

    /**
     * 获取k8s集群list
     * @return
     */
    List<ClusterInfoVO> list();

    /**
     * 添加cluster集群信息
     * @param createClusterVO
     * @return
     */
    ClusterInfoVO insertCluster(CreateClusterVO createClusterVO);

    /**
     * 删除k8s集群
     * @param clusterId
     * @return
     */
    ActionResponse delCluster(String clusterId);

    /**
     * 更新k8s集群信息
     * @param createClusterVO
     * @return
     */
    ClusterInfoVO updateCluster(CreateClusterVO createClusterVO);

    List<Namespace> getNamespace(String clusterId);

    List<K8sResourceCountVO> countResource(String userId);

    void createNamespace(String clusterId, String namespaceName);
}
