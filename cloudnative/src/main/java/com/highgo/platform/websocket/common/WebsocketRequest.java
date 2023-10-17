package com.highgo.platform.websocket.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wang_yangbj<br>
 * @version 1.0
 * 2018年11月16日 下午2:49:23<br>
 */
@Data
public class WebsocketRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String userId;
    private String userName;
    private String handlerName;
    private String instanceId;
    private String instanceStatus;
    private String operateType;
    private String operateStatus;
    private String messageType;
    private Object message;
    private List<String> tags;
}
