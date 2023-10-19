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

package com.highgo.platform.websocket.service;

import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.OperationDTO;
import com.highgo.platform.websocket.common.WebsocketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketServiceImpl.class);

    private String websocketAddr;

    @Value("${common.serviceName}")
    private String websocketHandler;

    @Autowired
    @Qualifier(value = "dbRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public void sendMessageToUser(WebsocketRequest message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            // headers.add("Authorization", "bearer " + KeycloakUtil.getKeyCloakToken());
            HttpEntity<Object> httpEntity = new HttpEntity<>(message, headers);
            ResponseEntity<String> response =
                    restTemplate.exchange(URI.create(websocketAddr), HttpMethod.POST, httpEntity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            String websocketInfo = mapper.writeValueAsString(message);
            logger.warn("send websocket info is: [{}]", websocketInfo);
            logger.warn("send websocket info status:{}", response.getStatusCode().value());
        } catch (Exception e) {
            logger.warn("send websocket info error", e);
        }
    }

    @Override
    public void sendMsgToUser(InstanceDTO instanceDTO, OperationDTO operationDTO) {
        if (true) {
            return;
        }
        WebsocketRequest wsRequest = new WebsocketRequest();
        wsRequest.setUserId(instanceDTO.getCreator());
        wsRequest.setUserName(instanceDTO.getAccount());
        wsRequest.setHandlerName(websocketHandler);
        wsRequest.setInstanceId(instanceDTO.getId());
        wsRequest.setInstanceStatus(String.valueOf(instanceDTO.getStatus()));
        wsRequest.setOperateType(String.valueOf(operationDTO.getName()));
        wsRequest.setOperateStatus(String.valueOf(operationDTO.getStatus()));
        wsRequest.setMessageType("");
        wsRequest.setMessage("");
        sendMessageToUser(wsRequest);
    }

}
