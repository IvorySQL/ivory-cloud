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

package com.highgo.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfo<T> {

    private int pageNo;
    private int pageSize;
    private long totalCount;
    private T data;

    public int getPageNo() {
        return pageNo;
    }

    //
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static final class builder<T> {

        private int pageNo;
        private int pageSize;
        private long totalCount;
        private T data;

        public builder() {
        }

        public builder pageNo(int pageNo) {
            this.pageNo = pageNo;
            return this;
        }

        public builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public builder totalCount(long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public builder data(T data) {
            this.data = data;
            return this;
        }
    }
}
