package com.highgo.cloud.auth.response.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.highgo.cloud.auth.response.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by pengq on 2019/9/5 18:23.
 */

public class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<RestResponse<Object>> {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    @Override
    public void serialize(RestResponse<Object> response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        logger.debug("Start serialize Response: {}", response);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(KeyDictionary.CODE_KEY, String.valueOf(response.getCode()));
        jsonGenerator.writeStringField(KeyDictionary.MESSAGE_KEY, response.getMessage());
        if (response.getData().isPresent()) {
            jsonGenerator.writeObjectField(KeyDictionary.DATA_KEY, response.getData().get());
        }

        if (response.getFields().isPresent()) {
            Map<String, Object> fields = response.getFields().get();
            fields.forEach((k, v) -> {
                try {
                    jsonGenerator.writeObjectField(k, v);
                } catch (IOException e) {
                    logger.error("write object field fail,key:[{}],value:[{}]", k, v);
                }
            });
        }

        jsonGenerator.writeEndObject();
        logger.debug("Finished serialize Response：{}", response);
    }
}
