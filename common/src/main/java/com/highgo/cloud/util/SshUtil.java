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

import cn.hutool.extra.ssh.JschUtil;

import com.highgo.cloud.constant.CommonConstant;

import com.highgo.cloud.exception.ShellException;
import com.highgo.cloud.model.K8sClusterInfoDTO;
import com.highgo.cloud.model.ServerConnectVO;
import com.highgo.cloud.model.ShellResult;
import com.jcraft.jsch.*;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author lucunqiao
 * @date 2023/2/9
 */
@Slf4j
public class SshUtil {

    /**
     * 连接服务器的超时时间
     */
    private static int timeout_connect_server = 30 * CommonConstant.MILLISECONDS;

    /**
     * 执行脚本的超时时间
     */
    private static long timeout_run_script = 3 * 60 * CommonConstant.MILLISECONDS;

    /**
     * description: 读取远程服务器的文件内容
     * date: 2023/7/20 17:46
     * @param server
     * @param filePath
     * @return: byte
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static byte[] readFile(ServerConnectVO server, String filePath)
            throws JSchException, SftpException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(server.getPassword());
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        // sftp.cd("/opt/highgo/hgdb-see-4.5.8/pdr");//上传时接文件的服务器的存放目录

        BufferedReader reader = new BufferedReader(new InputStreamReader(sftp.get(filePath), StandardCharsets.UTF_8));
        String str;
        StringBuffer sf = new StringBuffer();
        while ((str = reader.readLine()) != null) {
            sf.append(str);
        }
        reader.close();
        sftp.exit();
        session.disconnect();
        return sf.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * description: 远程执行shell命令
     * date: 2023/2/17 14:49
     * @param server
     * @return: String
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @SneakyThrows
    public static String remoteExeCommand(ServerConnectVO server) {

        log.info("[SshUtil.remoteExeCommand] begin to server [{}] execute command [{}]", server.getHost(),
                server.getCommand());

        JSch jsch = new JSch();
        Session session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(server.getPassword());
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(server.getCommand());
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String out = IOUtils.toString(in, "UTF-8");

        channelExec.disconnect();
        session.disconnect();

        return out;
    }

    // public static Map<Integer,String> remoteExeCommandReturnMap(ServerConnectVO server) {
    // Session session = null;
    // ChannelExec channelExec = null;
    // Map<Integer,String> hashMap = new HashMap();
    // try {
    // JSch jsch = new JSch();
    // session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
    // session.setConfig("StrictHostKeyChecking", "no");
    // session.setPassword(server.getPassword());
    // session.setTimeout(10 * 30 * 1000 );
    // session.connect();
    //
    // channelExec = (ChannelExec) session.openChannel("exec");
    // InputStream in = channelExec.getInputStream();
    // channelExec.setCommand(server.getCommand());
    // channelExec.setErrStream(System.err);
    // channelExec.connect();
    //
    // while (!channelExec.isClosed()) {
    // TimeUnit.MILLISECONDS.sleep(100);
    // }
    // int exitStatus = channelExec.getExitStatus();
    //
    // String out = IOUtils.toString(in, "UTF-8");
    // hashMap.put(exitStatus, out);
    //
    // log.info(out);
    //
    // return hashMap;
    // } catch (Exception e) {
    // log.error("Failed in remoteExeCommandReturnMap: ", e);
    // throw new ShellException(e);
    // } finally {
    // if (session != null) {
    // session.disconnect();
    // }
    // if (channelExec != null) {
    // channelExec.disconnect();
    // }
    // }
    // }

    /**
     * 2023.09.05 在用
     * 输出脚本执行的每一步详细信息，
     * 并汇总结果到返回值里
     * @param server
     * @return
     * @throws IOException 
     * @throws JSchException 
     * @throws InterruptedException 
     */
    public static Map<Integer, String> remoteExeCommandReturnMap(ServerConnectVO server) {
        return remoteExeCommandReturnMap(server, timeout_connect_server, timeout_run_script, false);

    }

    /**
     * 释放资源
     */
    private static void close(Session session, ChannelExec channelExec) {
        log.info("Release resource.");
        if (channelExec != null && channelExec.isConnected()) {
            channelExec.disconnect();
        }

        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
    /**
     * 去远程服务器执行脚本
     * 
     * @param server
     * @param connect_server_interval       ：连接服务器得超时时间
     * @param run_script：执行脚本得超时时间
     * @param killScript
     *        true: 需要kill 脚本进程
     *        false: 不需要kill 脚本进程
     * @return
     * @throws JSchException 
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static Map<Integer, String> remoteExeCommandReturnMap(ServerConnectVO server, int connect_server_interval,
            long run_script_interval, boolean killScript) {

        log.info(
                "Begin to execute command: [{}], connect server timeout: [{}] MilliSdconds, run script timeout: [{}] MilliSdconds, need kill script after timeout: [{}]",
                server.getCommand(), connect_server_interval, run_script_interval, killScript);

        Session session = null;
        ChannelExec channelExec = null;
        Map<Integer, String> hashMap = new HashMap<Integer, String>();
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(server.getPassword());
            session.setTimeout(connect_server_interval);
            session.connect();

            channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(server.getCommand());
            channelExec.setErrStream(System.err);
            channelExec.connect();

            // 执行脚本的开始时间
            long start = System.currentTimeMillis();

            while (!channelExec.isClosed()) {

                long duration = System.currentTimeMillis() - start;
                // log.info("run command for " + duration + " seconds, will timeout in : " +
                // run_script_interval + " seconds.");
                // 如果超过等待时间，则退出
                // if((System.currentTimeMillis()-start) > timeout_run_script){
                if (duration > run_script_interval) {
                    channelExec.disconnect();
                    log.warn("Timeout for running command: {}", server.getCommand());

                    if (killScript) {
                        killRemoteScript(session, server.getScriptName());
                    }
                    throw new ShellException("Timeout for running command: " + server.getCommand());
                }

                // if ((lineStr = br.readLine()) != null) {
                // log.info(lineStr);
                // shellOutBuffer.append(lineStr + System.getProperty("line.separator"));
                // }

                TimeUnit.MILLISECONDS.sleep(1000);
            }
            int exitStatus = channelExec.getExitStatus();
            String out = IOUtils.toString(in, "UTF-8");
            // hashMap.put(exitStatus, shellOutBuffer.toString());
            hashMap.put(exitStatus, out);

            return hashMap;
        } catch (Exception e) {
            log.error("Failed in remoteExeCommandReturnMap: ", e);
            throw new ShellException(e);
        } finally {
            close(session, channelExec);
        }

    }

    /**
     * kill 正在执行的脚本
     * @param session
     * @param scriptName
     */
    public static void killRemoteScript(Session session, String scriptName) {

        log.debug("Will kill Script: " + scriptName);
        ChannelExec channelExec = null;

        try { // 获取脚本进程ID
            String getPIDCommand = "pgrep -f " + scriptName; // 替换为你的脚本名称
            channelExec = (ChannelExec) session.openChannel("exec");

            channelExec.setCommand(getPIDCommand);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            InputStream in = channelExec.getInputStream();
            channelExec.connect();
            byte[] tmp = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while (in.read(tmp, 0, 1024) != -1) {
                stringBuilder.append(new String(tmp));
            }
            in.close();
            channelExec.disconnect();

            // 杀死进程
            String pid = stringBuilder.toString().trim().replaceAll(System.lineSeparator(), " ");
            String killCommand = "kill " + pid;
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(killCommand);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            channelExec.disconnect();

        } catch (JSchException e) {
            log.error("Failed to kill script: ", e);
        } catch (IOException e) {
            log.error("Failed to kill script: ", e);
        } catch (Exception e) {
            log.error("Failed to kill script: ", e);
        } finally {
            if (channelExec != null && channelExec.isConnected()) {
                channelExec.disconnect();
            }
        }

    }

    /**
     * 去远程服务器执行脚本
     * 
     * @param server
     * @param connect_server_interval ：连接服务器得超时时间
     * @return
     * @throws JSchException 
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static Map<Integer, String> remoteExeCommandReturnMap(ServerConnectVO server, int connect_server_interval) {

        return remoteExeCommandReturnMap(server, connect_server_interval, timeout_run_script, false);

    }

    /**
     * 去远程服务器执行脚本
     * 
     * @param server
     * @param run_script：执行脚本得超时时间
     * @return
     * @throws JSchException 
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static Map<Integer, String> remoteExeCommandReturnMap(ServerConnectVO server, long run_script_interval) {

        return remoteExeCommandReturnMap(server, timeout_connect_server, run_script_interval, false);

    }
    /**
     *  到远程服务器执行命令，并将返回结果封装到ShellResult
     *
     */
    public static ShellResult remoteExeCommandWithReturn(ServerConnectVO serverConnectVO) {
        Session session = null;
        ChannelExec channelExec = null;
        ShellResult result = new ShellResult();
        try {
            session = connectRemote(serverConnectVO.getHost(), String.valueOf(serverConnectVO.getPort()),
                    serverConnectVO.getUser(), serverConnectVO.getPassword());
            log.info("execute on ip:{}", serverConnectVO.getHost());
            log.info("cmd is {}", serverConnectVO.getCommand());
            channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(serverConnectVO.getCommand());
            channelExec.setErrStream(System.err);
            channelExec.connect();

            while (!channelExec.isClosed()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            int exitStatus = channelExec.getExitStatus();
            String out = IOUtils.toString(in, "UTF-8");
            result.setMessage(out);
            result.setReturnCode(exitStatus);
            log.info(out);

            return result;
        } catch (Exception e) {
            log.error("Failed in remoteExeCommandReturnMap: ", e);
            throw new ShellException(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
    }

    /**
     * description: 上传文件到远程服务器
     * date: 2023/2/17 14:50
     * @param server
     * @param localPath
     * @param remotePath
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    @SneakyThrows
    public static void uploadFile(ServerConnectVO server, String localPath, String remotePath) {

        log.info("[SshUtil.uploadFile] Server info is [{}]", server.toString());
        log.info("[SshUtil.uploadFile] upload file from local \"[{}]\" to server \"[{}]\"", localPath, remotePath);
        // String host = "127.0.0.1";//windos到linux用外网IP就可以,但linux上传到linux要涉及网段、防火墙等，所以这里用的是内网IP
        JSch jsch = new JSch();
        Session session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(server.getPassword());
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;
        sftp.cd(remotePath);// 上传时接文件的服务器的存放目录

        InputStream is = null;
        File inFile = new File(localPath);
        if (inFile.isDirectory()) {
            // 传入的是文件目录
            for (File file : inFile.listFiles()) {
                String fileName = file.getName();
                is = new FileInputStream(file);
                sftp.put(is, fileName, ChannelSftp.OVERWRITE);// 有重名文件覆盖
            }
        }

        if (inFile.isFile()) {
            // 传入的是文件
            is = new FileInputStream(inFile);
            sftp.put(is, inFile.getName(), ChannelSftp.OVERWRITE);// 有重名文件覆盖
        }
        session.disconnect();
        is.close();

    }

    /**
     * 2023.09.05 在用
     * 本地执行shell并返回结果
     * @param shellCmd
     * @param time 超时时间
     * @param unit
     * @return
     */
    public static boolean localExecShellWithTimeout(String shellCmd) {
        return localExecShellWithTimeout(shellCmd, timeout_connect_server, CommonConstant.DEFAULT_TIMEUNIT);
    }
    /**
     * 2023.09.05 在用
     * 本地执行shell并返回结果
     * @param shellCmd
     * @param time 超时时间
     * @param unit
     * @return
     */
    public static boolean localExecShellWithTimeout(String shellCmd, long time, TimeUnit unit) {
        boolean result = true;
        log.info("Begin to execute command: [{}], timeout: [{}] , unit: [{}] ", shellCmd, time, unit.name());
        try {
            Process ps = Runtime.getRuntime().exec(shellCmd);
            try {
                // input message
                new Thread(() -> {
                    try {
                        BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String lineStr;
                        while ((lineStr = br.readLine()) != null) {
                            log.info(lineStr);
                        }

                        log.info("Last return message is: [{}]", lineStr);

                        br.close();
                        in.close();
                    } catch (Exception e) {
                        if (!"Stream closed".equals(e.getMessage())) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }).start();

                // ERROR message
                new Thread(() -> {
                    try {
                        BufferedInputStream in = new BufferedInputStream(ps.getErrorStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String lineStr;
                        while ((lineStr = br.readLine()) != null) {
                            log.info(lineStr);
                        }
                        log.info("Last return message is: [{}]", lineStr);
                        br.close();
                        in.close();
                    } catch (Exception e) {
                        if (!"Stream closed".equals(e.getMessage())) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }).start();

                result = ps.waitFor(time, unit); // 0 is success, 1 for fail, -1 for exception
                log.info("Finished to execute command...... the resultCode is: [{}]", result);
            } catch (Exception e) {
                log.error("Exception fired during executing shell command: ", e);
                result = false;
            } finally {
                ps.destroy();
            }
        } catch (IOException e1) {
            log.error("Exception fired during executing shell command: ", e1);
        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
            result = false;
        }

        return result;

    }

    /**
     * description: 服务器本地local执行命令
     * date: 2023/4/18 10:35
     * @param shellCmd
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static <T> void localExecShell(T shellCmd) throws IOException {

        Process proc;
        if (shellCmd instanceof String) {
            proc = Runtime.getRuntime().exec((String) shellCmd);
        } else if (shellCmd instanceof String[]) {
            proc = Runtime.getRuntime().exec((String[]) shellCmd);
        } else {
            throw new RuntimeException("Unsupported type of shellCmd.");
        }

        try (
                BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                log.warn(lineStr);
            }
            log.warn("Last return message is: [{}]", lineStr);
            br.close();
            in.close();
            while (proc.isAlive()) {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
            int result = proc.waitFor();
            log.warn("Finished to execute command, the resultCode is:[{}]", result);
            if (result != 0) {
                throw new RuntimeException("Failed to execute command.");
            }

        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
        } finally {
            proc.destroy();
        }
    }

    /**
     * description: 复制jar包中classpath下单个文件到jar包的同级目录下
     * date: 2023/4/18 10:35
     * @param location
     * @return: String
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static String copy(String location) throws IOException {
        InputStream in = getResource("classpath:" + location);
        Path dist = getDistFile(location);
        Files.copy(in, dist);
        in.close();
        return dist.toAbsolutePath().toString();
    }

    /**
     * description: 获取资源文件输入流
     * date: 2023/4/18 10:36
     * @param location
     * @return: InputStream
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    private static InputStream getResource(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        InputStream in = resolver.getResource(location).getInputStream();
        byte[] byteArray = IOUtils.toByteArray(in);
        in.close();
        return new ByteArrayInputStream(byteArray);
    }
    /**
     * description: 获取项目所在文件夹的绝对路径
     * date: 2023/4/18 10:33
     * @param
     * @return: String
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static String getCurrentDirPath() {
        URL url = FileCopyUtils.class.getProtectionDomain().getCodeSource().getLocation();
        String path = url.getPath();
        if (path.startsWith("file:")) {
            path = path.replace("file:", "");
        }
        if (path.contains(".jar!/")) {
            path = path.substring(0, path.indexOf(".jar!/") + 4);
        }
        File file = new File(path);
        path = file.getParentFile().getAbsolutePath();
        return path;
    }

    private static Path getDistFile(String path) throws IOException {
        String currentRealPath = getCurrentDirPath();
        Path dist = Paths.get(currentRealPath + File.separator + path);
        Path parent = dist.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.deleteIfExists(dist);
        return dist;
    }

    /**
     * description: 从jar包中复制文件夹下的所有文件到jar所在目录（兼容ide运行）
     * date: 2023/4/18 10:31
     * @param dirPath  classpath的相对路径，如：monitor/dashboards 或者 monitor
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static void copyDirFilesFromJar(Class clazz, String dirPath) throws IOException {
        URL url = clazz.getClassLoader().getResource(dirPath);
        Assert.notNull(url, "Invalid path");
        String urlStr = url.toString();
        if (urlStr.contains(".jar!/") && urlStr.startsWith("jar:file:")) {
            // jar包中运行
            // 找到!/ 截断之前的字符串
            String jarPath = urlStr.substring(0, urlStr.indexOf("!/") + 2);
            URL jarURL = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            Assert.isTrue(jarEntrys.hasMoreElements());
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                // 判断路径，获取 resource下的文件
                String name = entry.getName();
                String pathDir = "BOOT-INF/classes/" + dirPath;
                if (name.startsWith(pathDir) && !entry.isDirectory()) {
                    String substring = name.substring("BOOT-INF/classes/".length());
                    SshUtil.copy(substring);
                }
            }
        } else {
            // ide运行
            File file = new File(url.getPath());
            List<File> allFileList = new ArrayList<>();
            getAllFile(file, allFileList);

            String osName = System.getProperty("os.name");
            String pathIntercept = dirPath;
            if (osName.startsWith("Mac OS")) {
                // 苹果
            } else if (osName.startsWith("Windows")) {
                // windows
                pathIntercept = dirPath.replace("/", "\\");
            } else {
                // unix or linux
            }
            // 复制文件
            for (File f : allFileList) {
                String absolutePath = f.getAbsolutePath();
                String fileName = absolutePath.substring(absolutePath.indexOf(pathIntercept));
                SshUtil.copy(fileName);
            }

        }

    }

    /**
     * description: 递归获取文件夹下的所有文件
     * date: 2023/4/18 10:33
     * @param fileInput
     * @param allFileList
     * @return: void
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static void getAllFile(File fileInput, List<File> allFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }

    // 获取本机ip
    public static String getLocalIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * description: 获取k8s集群master的连接信息
     * date: 2023/4/24 10:26
     * @param k
     * @return: ServerConnectVO
     * @author: highgo-lucunqiao
     * @since JDK 1.8
     */
    public static ServerConnectVO getServerConnectVO(K8sClusterInfoDTO k) {
        // 构建ssh连接server信息
        return ServerConnectVO
                .builder()
                .host(k.getServerUrl())
                .user(k.getServerUser())
                .password(k.getServerPass())
                .port(k.getServerSshport())
                .build();
    }

    /**
     *  读取服务器文件内容
     * @return 文件内容
     */
    @SneakyThrows
    public static String readFile(String ip, String port, String user, String password, String filePath) {
        // 连接远程服务器
        Session session = connectRemote(ip, port, user, password);
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        sftp.get(filePath, outputstream);
        String fileInfo = (outputstream.size() == 0) ? ("") : (outputstream.toString());
        JschUtil.close(sftp);
        JschUtil.close(session);
        log.info("read file str is [{}]", fileInfo);
        return fileInfo;
    }

    /**
     * 连接远程服务器返回session连接
     * @return Session
     */
    @SneakyThrows
    public static Session connectRemote(String ip, String port, String user, String password) {
        log.info("start to login remote server,ip:[{}],port:[{}],user:[{}],password:[{}]", ip, port, user, password);
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, ip, Integer.parseInt(port));
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        log.info("login remote server,ip:[{}],port:[{}],user:[{}],password:[{}] ,succeed!", ip, port, user, password);
        return session;
    }

    /**
     * 使用lombok抛出异常
     */
    @SneakyThrows
    public static void remoteExeCommandWithoutException(ServerConnectVO serverConnectVO) {
        remoteExeCommand(serverConnectVO);
    }
}
