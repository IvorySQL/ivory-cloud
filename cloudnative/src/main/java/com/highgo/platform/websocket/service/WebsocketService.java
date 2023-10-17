package com.highgo.platform.websocket.service;


import com.highgo.platform.apiserver.model.dto.InstanceDTO;
import com.highgo.platform.apiserver.model.dto.OperationDTO;
import com.highgo.platform.websocket.common.WebsocketRequest;

public interface WebsocketService {

    void sendMessageToUser(WebsocketRequest messageEntity);

    public void sendMsgToUser(InstanceDTO instanceDTO, OperationDTO operationDTO);

}
