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

package com.highgo.platform.operator.cr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highgo.platform.operator.cr.bean.DatabaseClusterSpec;
import com.highgo.platform.operator.cr.bean.DatabaseClusterStatus;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

@Group("ivory-operator.ivorysql.org")
@Version("v1beta1")
@Kind("IvoryCluster")
@Plural("ivoryclusters")
@Singular("ivorycluster")
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class DatabaseCluster extends CustomResource<DatabaseClusterSpec, DatabaseClusterStatus>
        implements
            Namespaced,
            Serializable {

    @Value("${cluster.group}")
    private String group;
    @Value("${cluster.version}")
    private String version;
    @Value("${cluster.kind}")
    private String kind;
    @Value("${cluster.plural}")
    private String plural;
    @Value("${cluster.singular}")
    private String singular;

    private void editAnnotation(Class clazz, String newValue) throws NoSuchFieldException, IllegalAccessException {
        // 这个代理实例所持有的 InvocationHandler
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(getClass().getAnnotation(clazz));
        // 获取 AnnotationInvocationHandler 的 memberValues 字段
        Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
        // 因为这个字段事 private final 修饰，所以要打开权限
        declaredField.setAccessible(true);
        // 获取 memberValues
        Map<String, String> memberValues = (Map) declaredField.get(invocationHandler);
        // 修改 value 属性值
        memberValues.put("value", newValue);
    }

    @PostConstruct
    public void init() {

        try {
            editAnnotation(Group.class, group);
            editAnnotation(Version.class, version);
            editAnnotation(Kind.class, kind);
            editAnnotation(Plural.class, plural);
            editAnnotation(Singular.class, singular);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
