package com.highgo.platform.apiserver.model.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: highgo-lucunqiao
 * @date: 2023/4/18 18:34
 * @Description: alert massage
 */
@NoArgsConstructor
@Data
public class AlertMessage {

    @JsonProperty("receiver")
    private String receiver;
    @JsonProperty("status")
    private String status;
    @JsonProperty("alerts")
    private List<Alerts> alerts;
    @JsonProperty("groupLabels")
    private GroupLabels groupLabels;
    @JsonProperty("commonLabels")
    private CommonLabels commonLabels;
    @JsonProperty("commonAnnotations")
    private CommonAnnotations commonAnnotations;
    @JsonProperty("externalURL")
    private String externalURL;
    @JsonProperty("version")
    private String version;
    @JsonProperty("groupKey")
    private String groupKey;
    @JsonProperty("truncatedAlerts")
    private Integer truncatedAlerts;

    @NoArgsConstructor
    @Data
    public static class GroupLabels {
        @JsonProperty("alertname")
        private String alertname;
        @JsonProperty("autoscaling")
        private String autoscaling;
        @JsonProperty("job")
        private String job;
        @JsonProperty("service")
        private String service;
        @JsonProperty("severity")
        private String severity;
    }

    @NoArgsConstructor
    @Data
    public static class CommonLabels {
        @JsonProperty("alertname")
        private String alertname;
        @JsonProperty("autoscaling")
        private String autoscaling;
        @JsonProperty("cluster")
        private String cluster;
        @JsonProperty("cluster_id")
        private String clusterId;
        @JsonProperty("deployment")
        private String deployment;
        @JsonProperty("instance")
        private String instance;
        @JsonProperty("instance_id")
        private String instanceId;
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("job")
        private String job;
        @JsonProperty("kubernetes_namespace")
        private String kubernetesNamespace;
        @JsonProperty("pg_cluster")
        private String pgCluster;
        @JsonProperty("pod")
        private String pod;
        @JsonProperty("role")
        private String role;
        @JsonProperty("server")
        private String server;
        @JsonProperty("service")
        private String service;
        @JsonProperty("severity")
        private String severity;
        @JsonProperty("severity_num")
        private String severityNum;
        @JsonProperty("type")
        private String type;
    }

    @NoArgsConstructor
    @Data
    public static class CommonAnnotations {
        @JsonProperty("summary")
        private String summary;
    }

    @NoArgsConstructor
    @Data
    public static class Alerts {
        @JsonProperty("status")
        private String status;
        @JsonProperty("labels")
        private Labels labels;
        @JsonProperty("annotations")
        private Annotations annotations;
        @JsonProperty("startsAt")
        private String startsAt;
        @JsonProperty("endsAt")
        private String endsAt;
        @JsonProperty("generatorURL")
        private String generatorURL;
        @JsonProperty("fingerprint")
        private String fingerprint;

        @NoArgsConstructor
        @Data
        public static class Labels {
            @JsonProperty("alertname")
            private String alertname;
            @JsonProperty("autoscaling")
            private String autoscaling;
            @JsonProperty("cluster")
            private String cluster;
            @JsonProperty("cluster_id")
            private String clusterId;
            @JsonProperty("deployment")
            private String deployment;
            @JsonProperty("instance")
            private String instance;
            @JsonProperty("instance_id")
            private String instanceId;
            @JsonProperty("ip")
            private String ip;
            @JsonProperty("job")
            private String job;
            @JsonProperty("kubernetes_namespace")
            private String kubernetesNamespace;
            @JsonProperty("pg_cluster")
            private String pgCluster;
            @JsonProperty("pod")
            private String pod;
            @JsonProperty("role")
            private String role;
            @JsonProperty("server")
            private String server;
            @JsonProperty("service")
            private String service;
            @JsonProperty("severity")
            private String severity;
            @JsonProperty("severity_num")
            private String severityNum;
            @JsonProperty("type")
            private String type;
        }

        @NoArgsConstructor
        @Data
        public static class Annotations {
            @JsonProperty("summary")
            private String summary;
        }
    }
}
