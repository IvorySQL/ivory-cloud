package com.highgo.cloud.auth.response.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.highgo.cloud.auth.response.GenericResponse;
import com.highgo.cloud.auth.response.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by pengq on 2019/9/5 18:22.
 */
public class JsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<RestResponse> {
    private static final Logger logger = LoggerFactory.getLogger(JsonDeserializer.class);
    @Override
    public RestResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        logger.trace("starting deserializer response...");
        ObjectNode nodes = jsonParser.getCodec().readTree(jsonParser);
        ResponseParser parser = new ResponseParser(nodes.get(KeyDictionary.CODE_KEY).asInt(), nodes.get(KeyDictionary.MESSAGE_KEY).asText());
        if (nodes.has(KeyDictionary.DATA_KEY)) {
            parser.withData(nodes.get(KeyDictionary.DATA_KEY));
        }
        ObjectNode jsonNodes = nodes.remove(Arrays.asList(KeyDictionary.CODE_KEY, KeyDictionary.MESSAGE_KEY, KeyDictionary.DATA_KEY));
        int size = jsonNodes.size();
        if (size != 0) {
            Iterator<Map.Entry<String, JsonNode>> iterator = jsonNodes.fields();
            iterator.forEachRemaining(x -> parser.appendField(x.getKey(), x.getValue()));
        }
        logger.trace("deserializer response finished...");
        return parser;
    }

    private static class ResponseParser extends GenericResponse {
        ResponseParser(int code, String message) {
            super(code, message);
        }
    }
}
