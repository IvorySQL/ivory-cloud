package com.highgo.cloud.util;

import com.highgo.cloud.constant.CommonConstant;
import com.highgo.cloud.constant.DataConstant;
import com.highgo.cloud.model.CommonResult;
import com.highgo.cloud.model.ResultTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 公共工具类
 *
 * @author chushaolin Description: 2020-10-14
 */
@Slf4j
public class ServiceUtil {
	public static HashMap<String, String> loginInfo = new HashMap<String, String>();

	/**
	 * 拷贝多个文件到远程服务器 多个文件必须在同一文件夹下！
	 *
	 * @param ip         远程server ip
	 * @param password   远程server pwd
	 * @param scriptPath scp脚本路径 /opt/deployShell/common
	 * @param filePath   拷贝的文件路径
	 * @param fileNames  拷贝的文件名
	 * @param sshPort    远程ssh端口
	 * @return
	 */
	public static boolean scpFile2Server(String ip, String password, String scriptPath, String filePath, String sshPort,
			List<String> fileNames) {
		log.info("Enter common function : scpFile2Server.");
		try {
			StringBuffer s = new StringBuffer();
			for (String file : fileNames) {
				s.append(file + " ");
			}
			String fileList = s.substring(0, s.length() - 1);
			log.info("fileList = [{}]", fileList);
			int copyFile = ShellUtil.copyFile(ip, password, fileList, scriptPath, filePath, sshPort);
			if (copyFile != CommonConstant.SUCCESS) {
				throw new RuntimeException("copy file to server failed");
			}
			return true;
		} catch (Exception e) {
			log.error("Error:", e);
			return false;
		}
	}

	/**
	 * common result 2 result
	 *
	 * @param commonResult
	 * @return
	 */
	public static ResultTO common2Result(CommonResult commonResult) {
		ResultTO result = new ResultTO();
		result.setSuccess(commonResult.isResult());
		result.setResultcode(commonResult.getCode());
		result.setMessage(commonResult.getMessage());
		return result;
	}

	/**
	 * set result
	 *
	 * @param commonResult
	 * @param success
	 * @param message
	 * @param code
	 */
	public static void setCommonResult(CommonResult commonResult, boolean success, String message, int code) {
		commonResult.setResult(success);
		commonResult.setMessage(message);
		commonResult.setCode(code);
	}


	/**
	 * 设置免密登录
	 *
	 * @param ip
	 * @param user
	 * @param password
	 * @return
	 */
	public static Boolean sshWithoutPasswd(String ip, String user, String password) {
		String script = "/opt/deployShell/common/set_cloudLoginless_withOtherserver.sh";
		String shell = script + " " + ip + " " + password + " " + user;
		int result = ShellUtil.execShell(shell);
		if (result != CommonConstant.SUCCESS) {
			return false;
		}
		return true;
	}

	/**
	 * 创建用户存储空间(数据导入导出使用)
	 * 
	 * @param uName
	 */
	public static void createUserDir(String uName) {
		String localUserPath = System.getProperty("user.dir") + File.separator + ".." + File.separator
				+ DataConstant.TEM_FILE_DIR + File.separator + DataConstant.DB_EXPORT_PATH + File.separator + uName;
		File file = new File(localUserPath);
		file.mkdirs();
	}

}
