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
