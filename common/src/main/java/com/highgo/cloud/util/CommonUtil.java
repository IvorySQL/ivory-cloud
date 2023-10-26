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

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j

public class CommonUtil {

    /**
     * 校验(clusterId,namespaceName)在命名空间集合{"cluster1":["ns1","ns2"], "cluster2":["ns2","ns3"]}中存在
     * @return
     */
    public static boolean isNsMapContainNs(Map<String, List<String>> map, String clusterId, String namespace) {
        boolean flag = false;
        List<String> namespaces = map.get(clusterId);
        if (namespaces != null && namespaces.contains(namespace)) {
            flag = true;
        }
        return flag;
    }

    private CommonUtil() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T getEnum(Class<T> enumType, String name) {
        T e = null;
        try {
            // e = (T)
            Class t = enumType;
            e = (T) Enum.valueOf(t, name);
        } catch (Exception ex) {
            // 忽略所有异常
        }
        return e;
    }

    /**
     * 判断是否是枚举类型（java5）
     * @param enumClass
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEnum(Class enumClass) {
        while (enumClass.isAnonymousClass()) {
            enumClass = enumClass.getSuperclass();
        }
        return enumClass.isEnum();
    }

    /**
     * 判断是否为原始类型
     */
    @SuppressWarnings("rawtypes")
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNumber(Class cls) {
        return cls.getSuperclass().equals(Number.class) || CommonUtil.isWrapClass(cls) || cls.isPrimitive();
    }

    /**
     * 清理数组中值为空的元素
     * @param arrayObject
     * @return List/Object/
     */
    @SuppressWarnings("rawtypes")
    public static List<Object> clearNulls(Object arrayObject) {
        List<Object> objs = new ArrayList<Object>();
        if (arrayObject != null) {
            if (arrayObject.getClass().isArray()) {
                int length = Array.getLength(arrayObject);
                for (int i = 0; i < length; i++) {
                    Object obj = Array.get(arrayObject, i);
                    if (obj != null && !obj.toString().trim().equals("")) {
                        objs.add(obj);
                    }
                }
            } else if (isIterator(arrayObject)) {
                Iterable it = (Iterable) arrayObject;
                Iterator itor = it.iterator();
                while (itor.hasNext()) {
                    Object obj = itor.next();
                    if (obj != null && !obj.toString().trim().equals("")) {
                        objs.add(obj);
                    }
                }
            }
        }
        return objs;
    }

    public static boolean isIterator(Class clazz) {
        boolean isIterator = false;
        isIterator = Iterable.class.isAssignableFrom(clazz);
        return isIterator;
    }

    public static boolean isIterator(Object obj) {
        if (obj == null) {
            return false;
        }
        return isIterator(obj.getClass());
    }

    /**
     * Convert a list to an array
     * @param cls
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] buildArray(Class<T> cls, List<T> values) {
        T[] array = (T[]) Array.newInstance(cls, values.size());
        int i = 0;
        for (T t : values) {
            array[i] = t;
            i++;
        }
        return array;
    }

    /**
     * str base64加密
     * @param str
     * @return
     */
    public static String base64(String str) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes());

    }

    /**
     * base64 解密
     * @param str
     * @return
     */
    public static String unBase64(String str) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(str);
        return new String(decode);
    }

    public static Date stringToDate(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return simpleDateFormat.parse(dateStr);
    }

    public static String dateToStr(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 获取当前UTC时间
    public static Date getUTCDate() {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String utc = LocalDateTime.now(ZoneOffset.UTC).format(df);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.parse(utc);
        } catch (ParseException e) {

        }
        return null;
    }
    public static Date dateToGMT8(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 8);
        return calendar.getTime();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否时整形
     *
     * @param string 字符串
     * @return 是/否
     */
    public static boolean isInt(String string) {
        if (string == null)
            return false;

        String regEx1 = "[\\-|\\+]?\\d+";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断list是否为空
     *
     * @param <T>
     * @param <T>
     * @param list
     * @return
     */
    public static <T> boolean isEmpty(List<T> list) {
        boolean empty = true;

        if (null != list) {
            empty = list.isEmpty();
        }
        return empty;
    }

    /**
     * ip是否ping通
     *
     * @param ip
     * @return
     */
    public static Boolean ping(String ip) throws IOException {
        return InetAddress.getByName(ip).isReachable(3000);
    }

    /**
     * 获取ssh命令
     * @param user
     * @param ip
     * @return
     */
    public static String getSshCmd(String user, String ip) {
        return "ssh " + user + "@" + ip + " ";
    }

    /***
     * description: 测试ip:port是否能通
     * date: 2023/3/23 14:17
     * @param ip
     * @param port
     * @return: boolean
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static boolean connectIpAndPort(String ip, Integer port) {
        Socket connect = new Socket();
        boolean res = false;
        try {
            connect.connect(new InetSocketAddress(ip, port), 1000);// 建立连接
            // 能connect通返回true，否则返回false
            res = connect.isConnected();// 通过现有方法查看连通状态
        } catch (Exception e) {
            log.error("This [{}] cannot be connected", ip + ":" + port);
            log.error("ERROR:", e);
        } finally {
            try {
                connect.close();
            } catch (Exception e) {
                log.error("Close connection error:", e);
            }
        }
        return res;
    }

    /**
     * 获取当前时区。 中国时区
     * 云平台使用的  --------2023.08.17
     * @return
     */
    public static Date getCurrentGMT8() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+:08:00"));
        Date currentDate = calendar.getTime();
        return currentDate;
    }

    /**
     * timestamp 转成String
     *
     * @param date
     * @return
     */
    public static String timeStampToString(Timestamp date) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        if (null != date) {
            try {
                String dateStr = sdf.format(date);
                return dateStr;
            } catch (Exception e) {
                log.error("Exception is fired during converting timestamp", e);
                return "";
            }
        } else {
            return "";
        }

    }

    /**
     * 打印当前时间。
     */
    public static void printCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");// 设置日期格式
        log.debug(df.format(new Date()));// new Date()为获取当前系统时间
    }

    /**
     * 判断map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 获取系统当前时间 当作文件名
     * @return
     */
    public static String getCurrentTimeAsFileName() {
        Date currentDate = getCurrentGMT8();

        return DateHelper.getStrAsFileName(currentDate);
    }

}
