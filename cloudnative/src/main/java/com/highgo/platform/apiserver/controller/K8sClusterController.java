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

package com.highgo.platform.apiserver.controller;

import com.highgo.platform.apiserver.model.vo.request.CreateClusterVO;
import com.highgo.platform.apiserver.model.vo.response.ActionResponse;
import com.highgo.platform.apiserver.model.vo.response.ClusterInfoVO;
import com.highgo.platform.apiserver.model.vo.response.K8sResourceCountVO;
import com.highgo.platform.apiserver.service.K8sClusterService;
import io.fabric8.kubernetes.api.model.Namespace;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lucunqiao
 * @date 2023/2/8
 */
@Validated
@RestController
@RequestMapping("${common.request-path-prefix}/${common.version}")
@Api(value = "k8s集群信息", tags = {"k8s集群信息接口"})
public class K8sClusterController {

    @Resource(name = "k8sClusterService")
    private K8sClusterService k8sClusterService;

    /**
     * 所有k8s集群列表
     *
     * @return
     */
    @ApiOperation(value = "k8s集群列表", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters", method = RequestMethod.GET)
    public List<ClusterInfoVO> list() {
        return k8sClusterService.list();
    }

    /**
     * 添加k8s集群
     *
     * @return
     */
    @ApiOperation(value = "添加k8s集群", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters/add", method = RequestMethod.POST)
    public ClusterInfoVO insertCluster(@RequestBody @Validated CreateClusterVO createClusterVO) {
        return k8sClusterService.insertCluster(createClusterVO);
    }

    /**
     * 删除k8s集群
     *
     * @return
     */
    @ApiOperation(value = "删除k8s集群", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters/{clusterId}", method = RequestMethod.DELETE)
    public ActionResponse delCluster(@PathVariable String clusterId) {
        return k8sClusterService.delCluster(clusterId);
    }

    /**
     * 更新k8s集群
     *
     * @return
     */
    @ApiOperation(value = "更新k8s集群", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters/update", method = RequestMethod.POST)
    public ClusterInfoVO updateCluster(@RequestBody @Validated CreateClusterVO createClusterVO) {
        return k8sClusterService.updateCluster(createClusterVO);
    }

    /**
     * 获取k8s集群下的namespace
     *
     * @return
     */
    @ApiOperation(value = "获取k8s集群下的namespace", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters/{clusterId}/namespace", method = RequestMethod.GET)
    public List<Namespace> getNamespace(@PathVariable String clusterId) {
        return k8sClusterService.getNamespace(clusterId);
    }

    /**
     * 获所有k8s集群资源统计
     *
     * @return
     */
    @ApiOperation(value = "所有k8s集群资源统计", notes = "", tags = {"OpenAPI"})
    @RequestMapping(value = "/clusters/{userId}/resource", method = RequestMethod.GET)
    public List<K8sResourceCountVO> countResource(@PathVariable("userId") String userId) {
        return k8sClusterService.countResource(userId);
    }

}
