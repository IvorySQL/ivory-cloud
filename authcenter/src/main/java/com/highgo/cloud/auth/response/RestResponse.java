package com.highgo.cloud.auth.response;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.highgo.cloud.auth.response.serializer.JsonDeserializer;
import com.highgo.cloud.auth.response.serializer.JsonSerializer;

import java.util.Map;
import java.util.Optional;
@JsonDeserialize(using = JsonDeserializer.class)
@JsonSerialize(using = JsonSerializer.class)
public interface RestResponse<T> extends IErrorCode {
    Optional<Map<String, Object>> getFields();
    Optional<T> getData();
}
