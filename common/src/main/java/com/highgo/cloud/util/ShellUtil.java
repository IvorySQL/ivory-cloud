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

import com.highgo.cloud.model.ShellResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * 执行shell脚本
 * @author chushaolin
 *
 */
@Slf4j
public class ShellUtil {

    /**
     * 执行shell 脚本，并返回结果
     * @param shellCmd
     * @return 0:  success
     *         1:  failed
     *         -1: exception
     */
    public static int execShellSpaceParam(String[] shellCmd) {
        int resultCode = -1;

        log.debug("Begin to execute command: [{}]", Arrays.toString(shellCmd));

        try {
            Process ps = Runtime.getRuntime().exec(shellCmd);
            // -- catch the deploy log start
            BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String lineStr;

            while ((lineStr = br.readLine()) != null) {
                log.info(lineStr);
            }

            log.debug("Last return message is: [{}]", lineStr);
            br.close();
            in.close();
            resultCode = ps.waitFor(); // 0 is success, 1 for fail, -1 for exception
            log.debug("Finished to execute command...... the resultCode is: [{}]", resultCode);

        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
            resultCode = -1;
        }

        return resultCode;

    }

    /**
     * 执行shell 脚本，并返回结果
     * @param shellCmd
     * @return 0:  success
     *         1:  failed
     *         -1: exception
     * @throws IOException 
     */
    public static int execShell(String shellCmd) {
        // int resultCode = -1;
        int resultCode = 0;

        log.info("Begin to execute command: [{}]", shellCmd);

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
                // -- catch the deploy log start
                // BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
                // BufferedReader br = new BufferedReader(new InputStreamReader(in));
                //
                // String lineStr;
                //
                // while ((lineStr = br.readLine()) != null) {
                // log.debug(lineStr);
                // }
                //
                // log.info("Last return message is: {}", lineStr);
                //
                // br.close();
                // in.close();
                log.info("-------------will get shell result ");
                // TODO:::::::::::::::: 20230715
                resultCode = ps.waitFor(); // 0 is success, 1 for fail, -1 for exception
                log.debug("Finished to execute command...... the resultCode is: [{}]", resultCode);

            } catch (Exception e) {
                log.error("Exception fired during executing shell command: ", e);
                resultCode = -1;
            } finally {
                ps.destroy();
            }
        } catch (IOException e1) {
            log.error("Exception fired during executing shell command: ", e1);
            resultCode = -1;
        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
            resultCode = -1;
        }

        return resultCode;

    }

    public static boolean execShellWithTimeout(String shellCmd, long time, TimeUnit unit) {
        boolean result = true;
        log.info("Begin to execute command: [{}], timeout: [{}], unit: [{}]", shellCmd, time, unit.name());
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
     * execShells
     * @param shellCmd
     * @return
     */
    public static ShellResult execShells(String shellCmd) {
        ShellResult result = new ShellResult();
        int resultCode = -1;

        log.info("Begin to execute command: [{}]", shellCmd);
        String str = "";
        try {
            // ProcessBuilder 接收带空格的命令会报error =2 找不到文件错误
            // 把脚本中的空格转换下 ，Runtime.getRuntime().exec()源码中是按此种方法转换为数组。
            StringTokenizer st = new StringTokenizer(shellCmd);
            String[] cmdarray = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++) {
                cmdarray[i] = st.nextToken();
            }
            // 使用ProcessBuilder 创建process，把错误流与输出流合并，即可从输入流中读取所有的流信息，避免java进程被阻塞卡死。
            ProcessBuilder processBuilder = new ProcessBuilder(cmdarray);
            // 设置为true 标准错误将与标准输出合并，合并的数据可从 Process.getInputStream() 返回的流读取
            processBuilder.redirectErrorStream(true);
            Process ps = processBuilder.start();
            // Process ps = Runtime.getRuntime().exec(shellCmd);
            // -- catch the deploy log start
            BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String lineStr;

            while ((lineStr = br.readLine()) != null) {
                str = lineStr;
                log.debug(lineStr);
            }
            result.setMessage(str);
            log.info("Return message is: [{}]", str);
            br.close();
            in.close();
            resultCode = ps.waitFor(); // 0 is success, 1 for fail, -1 for exception
            log.info("Finished to execute command......");
            log.info("the resultCode is: [{}]", resultCode);
        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
            log.error("ERROR:", e);
            resultCode = -1;
        }
        result.setReturnCode(resultCode);
        log.info("Last return result is: [{}]", result);
        return result;

    }

    /**
     * execShells
     * @param shellCmd
     * @return
     */
    public static ShellResult execShellGetAllResult(String shellCmd) {
        ShellResult result = new ShellResult();
        int resultCode = -1;

        log.info("Begin to execute command: [{}]", shellCmd);
        String str = "";
        try {
            Process ps = Runtime.getRuntime().exec(shellCmd);
            // -- catch the deploy log start
            BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineStr = br.readLine()) != null) {
                stringBuilder.append(lineStr).append('\n');
            }
            str = stringBuilder.toString();
            log.info(str);
            result.setMessage(str);
            log.info("Last return message is: [{}]", str);
            br.close();
            in.close();
            resultCode = ps.waitFor(); // 0 is success, 1 for fail, -1 for exception
            log.info("Finished to execute command......");
            log.info("the resultCode is: [{}]", resultCode);
        } catch (Exception e) {
            log.error("Exception fired during executing shell command: ", e);
            log.error("ERROR:", e);
            resultCode = -1;
        }
        result.setReturnCode(resultCode);
        log.info("Last return result is: [{}]", result);
        return result;

    }

    /**
     * 拷贝文件
     * @param ip
     * @param passwd
     * @param fileList
     * @param dbScriptPath
     * @param filePath
     * @param sshPort
     * @return
     */
    public static int copyFile(String ip, String passwd, String fileList,
            String dbScriptPath, String filePath, String sshPort) {

        log.debug("scp file to dbserver.........");
        log.debug("ip: [{}]", ip);
        log.debug("passwd: [{}]", passwd);
        log.debug("fileList:[{}]", fileList);
        log.debug("dbScriptPath:[{}]", dbScriptPath);
        log.debug("filePath:[{}]", filePath);
        log.debug("sshPort:[{}]", sshPort);

        sshPort = StringUtils.isBlank(sshPort) ? "22" : sshPort;
        String[] commands = new String[]{dbScriptPath + "/scp_script.sh", ip, passwd, fileList, filePath, sshPort};

        int resultCode = execShellSpaceParam(commands);
        return resultCode;
    }
}
