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

package com.highgo.cloud.constant;

public class DataConstant {

    /**
     * 最大上传文件大小100MB
     */
    public static final int MAX_UPLOAD_SIZE = 100;

    /**
     * 最大上传文件大小单位
     */
    public static final String MAX_UPLOAD_UNIT = "MB";

    /**
     * 文件夹名
     */
    public static final String TEM_FILE_DIR = "tempFile";

    /**
     * 服务器已经init
     */
    public static final int NFS_SERVER_INITED = 1;

    /**
     * 服务器还没init
     */
    public static final int NFS_SERVER_UNINITED = 0;

    public static final int NFS_SERVER_INITING = 2;

    /**
     * 服务器init失败
     */
    public static final int NFS_SERVER_INIT_FAILED = -1;

    /**
     * 表不存在
     */
    public static final int TABLE_NOT_EXIST = 0;

    /**
     * 数据导入导出挂载路径
     */
    public static final String DB_EXPORT_PATH = "dbTableExport";
}
