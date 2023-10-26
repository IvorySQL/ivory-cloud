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

package com.highgo.cloud.util.toolkit;

import com.google.gson.*;
import org.springframework.lang.NonNull;

/**
 * Json builder
 * @author renlizou
 *
 */
public abstract class JsonBuilder {

    @NonNull
    @Override
    public String toString() {
        return build();
    }

    public abstract String build();

    public static ObjectBuilder forObject() {
        return new ObjectBuilder();
    }

    public static ArrayBuilder forArray() {
        return new ArrayBuilder();
    }

    public static ArrayBuilder forArray(int capacity) {
        return new ArrayBuilder(capacity);
    }

    public static ObjectBuilder fromObject(String json) {
        return new ObjectBuilder(json);
    }

    public static ArrayBuilder fromArray(String json) {
        return new ArrayBuilder(json);
    }

    public static class ObjectBuilder extends JsonBuilder {

        private final JsonObject object;

        ObjectBuilder() {
            this.object = new JsonObject();
        }

        ObjectBuilder(String json) {
            object = JsonParser.parseString(json).getAsJsonObject();
        }

        public ObjectBuilder with(String key, JsonElement element) {
            object.add(key, element);
            return this;
        }

        public ObjectBuilder with(String key, String value) {
            object.addProperty(key, value);
            return this;
        }

        public ObjectBuilder with(String key, Number value) {
            object.addProperty(key, value);
            return this;
        }

        public ObjectBuilder with(String key, boolean value) {
            object.addProperty(key, value);
            return this;
        }

        public ObjectBuilder with(String key, Character character) {
            object.addProperty(key, character);
            return this;
        }

        @Override
        public String build() {
            return object.toString();
        }

        public JsonObject toJsonObject() {
            return object;
        }
    }

    public static class ArrayBuilder extends JsonBuilder {

        private final JsonArray array;

        ArrayBuilder(String json) {
            array = JsonParser.parseString(json).getAsJsonArray();
        }

        ArrayBuilder(int capacity) {
            this.array = new JsonArray(capacity);
        }

        ArrayBuilder() {
            this.array = new JsonArray();
        }

        public ArrayBuilder add(boolean value) {
            array.add(value);
            return this;
        }

        public ArrayBuilder add(String value) {
            array.add(value);
            return this;
        }

        public ArrayBuilder add(Character character) {
            array.add(character);
            return this;
        }

        public ArrayBuilder add(Number number) {
            array.add(number);
            return this;
        }

        public ArrayBuilder add(JsonElement json) {
            array.add(json);
            return this;
        }

        public ArrayBuilder set(int index, boolean value) {
            array.set(index, new JsonPrimitive(value));
            return this;
        }

        public ArrayBuilder set(int index, String value) {
            array.set(index, new JsonPrimitive(value));
            return this;
        }

        public ArrayBuilder set(int index, Character character) {
            array.set(index, new JsonPrimitive(character));
            return this;
        }

        public ArrayBuilder set(int index, Number number) {
            array.set(index, new JsonPrimitive(number));
            return this;
        }

        public ArrayBuilder set(int index, JsonElement element) {
            array.set(index, element);
            return this;
        }

        @Override
        public String build() {
            return array.toString();
        }

        public JsonArray toJsonArray() {
            return array;
        }
    }
}
