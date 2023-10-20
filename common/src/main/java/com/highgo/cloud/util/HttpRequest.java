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
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpRequest {

    /**
     * 向指定URL发送GET方法的请求
     * @param url  发送请求的URL
     * @param param   请求参数，格式：name1=value1&name2=value2
     * @return String 响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        String urlNameString = "";
        try {
            if (CommonUtil.isEmpty(param)) {
                urlNameString = url;
            } else {
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                log.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！", e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，格式： name1=value1&name2=value2
     * @return  响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送 POST 请求出现异常！", e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送post请求
     * @param url  路径
     * @param t  参数对象
     * @return
     */
    public static <T> String sendPostJSONObj(String url, T t) throws IOException {
        String body = "";
        // 创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        String objectJSON = FastJsonUtils.convertObjectToJSON(t);
        // 装填参数
        StringEntity strEntity = new StringEntity(objectJSON, "utf-8");
        // strEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        // 设置参数到请求对象中
        httpPost.setEntity(strEntity);
        log.info("the request url of monitor config : [{}]", url);
        // 设置header信息
        // 指定报文头【Content-type】、【User-Agent】
        // httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        // 获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // 按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);
        // 释放链接
        response.close();
        return body;
    }

    /**
     * 发送post请求
     * @param url  路径
     * @param json  参数对象
     * @return
     */
    public static <T> String sendPostJSONStr(String url, String json) throws IOException {
        String body = "";
        // 创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        // 装填参数
        StringEntity strEntity = new StringEntity(json, "utf-8");
        // strEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        // 设置参数到请求对象中
        httpPost.setEntity(strEntity);
        log.info("the request url of monitor config ：[{}]", url);
        // 设置header信息
        // 指定报文头【Content-type】、【User-Agent】
        // httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        // 获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // 按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);
        // 释放链接
        response.close();
        return body;
    }

    /**
     * 携带apikey的post请求
     * @param path
     * @param data
     * @param apiKey
     * @return
     */
    public static String apiKeyPostRequest(String path, String data, String apiKey) {
        try {
            URL url = new URL(path);
            // 打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            /**设置URLConnection的参数和普通的请求属性****start***/
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");// GET和POST必须全大写
            conn.connect();
            // POST请求
            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out1.write(data);
            out1.flush();
            out1.close();
            // 获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            // 构造一个字符流缓存
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                str = new String(str.getBytes(), "UTF-8");// 解决中文乱码问题
                System.out.println(str);
            }
            // 关闭流
            is.close();
            // 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            // 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();
            System.out.println("success");
            return str;
        } catch (Exception e) {
            log.error("apiKeyPostRequest failed.", e);
        }
        return null;
    }

    /**
     * 不携带apikey的post请求
     * Basic验证用户身份  携带用户名密码
     * @param path
     * @param data
     * @param autuserpwd
     * @return
     */
    public static String noKeyPostRequest(String path, String data, String autuserpwd) {
        try {

            // String authString = "admin:highgo";
            byte[] authEncBytes = Base64.encodeBase64(autuserpwd.getBytes("utf-8"));
            String authStringEnc = new String(authEncBytes);
            URL url = new URL(path);
            // 打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            /**设置URLConnection的参数和普通的请求属性****start***/
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            // request.addHeader("Authorization", "Basic " + authStringEnc);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");// GET和POST必须全大写

            conn.connect();
            // POST请求
            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out1.write(data);
            out1.flush();
            out1.close();
            // 获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            // 构造一个字符流缓存
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                str = new String(str.getBytes(), "UTF-8");// 解决中文乱码问题
                System.out.println(str);
            }
            // 关闭流
            is.close();
            // 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            // 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();
            System.out.println("success");
            return str;
        } catch (Exception e) {
            log.error("noKeyPostRequest failed.", e);
        }
        return null;
    }

    /**
     * 携带apikey的get请求   无数据
     * @param path
     * @param apiKey
     * @return
     */
    public static String apiKeyGetRequest(String path, String apiKey) {
        try {
            URL url = new URL(path);
            // 打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            /**设置URLConnection的参数和普通的请求属性****start***/
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");// GET和POST必须全大写
            conn.connect();
            // //POST请求
            // BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            // out1.flush();
            // out1.close();
            // 获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            // 构造一个字符流缓存
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                str = new String(str.getBytes(), "UTF-8");// 解决中文乱码问题
                System.out.println(str);
            }
            // 关闭流
            is.close();
            // 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            // 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();
            System.out.println("success");
            return str;
        } catch (Exception e) {
            log.error("apiKeyGetRequest failed.", e);
        }
        return null;
    }

    /***
     * @description 调用grafana api修改用户密码
     *
     * @param: changePasswordUrl
     * @return CloseableHttpResponse
     * @author srk
     * @date 2023/5/23 16:07
     */
    public static CloseableHttpResponse sendPut(String url, String authStringEnc, StringEntity strEntity)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);

        // 设置请求头
        httpPut.setHeader("Authorization", authStringEnc);
        httpPut.setHeader("Content-type", "application/json");
        httpPut.setEntity(strEntity);

        CloseableHttpResponse response = httpClient.execute(httpPut);
        return response;
    }

    /***
     * @description 调用grafana api新建用户
     *
     * @param: url
     * @param: authStringEnc
     * @param: strEntity
     * @return CloseableHttpResponse
     * @author srk
     * @date 2023/5/24 10:33
     */
    public static CloseableHttpResponse sendPostWithAuthorization(String url, String authStringEnc,
            StringEntity strEntity) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        httpPost.setHeader("Authorization", authStringEnc);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(strEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return response;
    }
    /***
     * @description 发送post请求，请求体为json格式 ，返回response
     *
     * @param: url
     * @param: strEntity
     * @return CloseableHttpResponse
     * @author srk
     * @date 2023/5/30 14:09
     */
    public static CloseableHttpResponse sendPostWithJsonBody(String url, StringEntity strEntity) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(strEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return response;
    }

    /***
     * @description 使用apiKey认证调用grafana api
     *
     * @param: url
     * @param: apiKey
     * @param: strEntity
     * @return CloseableHttpResponse
     * @author srk
     * @date 2023/5/24 17:00
     */
    public static CloseableHttpResponse sendPostWithApiKey(String url, String apiKey, StringEntity strEntity)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Content-type", "application/json");

        httpPost.setEntity(strEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return response;
    }

    /***
     * @description 调用grafana api 删除用户
     *
     * @param: url
     * @param: authStringEnc
     * @param: strEntity
     * @return CloseableHttpResponse
     * @author srk
     * @date 2023/5/24 10:34
     */
    public static CloseableHttpResponse sendDeleteWithAuthorization(String url, String authStringEnc)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);

        // 设置请求头
        httpDelete.setHeader("Authorization", authStringEnc);
        httpDelete.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        return response;
    }

}
