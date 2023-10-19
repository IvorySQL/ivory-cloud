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

package com.highgo.cloud.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TimeUtil {

    /**
     * saas  string数字转化为timestamp
     * @param timeString
     * @return
     */
    public static Timestamp convertTimeToTimestamp(String timeString) {
        String timeFormat = "yyyyMMddHHmmssSSS";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts;
        } catch (Exception e) {
            log.error("convertTimeToTimestamp failed.", e);
        }
        return null;
    }

    public static Timestamp convertTimeToSecond(String timeString) {
        String timeFormat = "yyyyMMddHHmmss";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts;
        } catch (Exception e) {
            log.error("convertTimeToSecond failed.", e);
        }
        return null;
    }

    public static String convertNumFormat(String timeString) {
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdformat = new SimpleDateFormat(timeFormat);
        try {
            Date d = null;
            d = sdformat.parse(timeString);
            Timestamp ts = new Timestamp(d.getTime());
            return ts.toString();
        } catch (Exception e) {
            log.error("convertNumFormat failed.", e);
        }
        return null;
    }
}
