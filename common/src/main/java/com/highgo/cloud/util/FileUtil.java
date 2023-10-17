package com.highgo.cloud.util;

import com.highgo.cloud.constant.CommonConstant;
import com.highgo.cloud.constant.DataConstant;
import com.highgo.cloud.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileUtil {
    /**
     * 上传文件到服务器
     *
     * @param file
     * @param path
     * @return 0上传成功   1上传失败
     */
    public static CommonResult fileUpload(MultipartFile file, String path) throws IOException {

        log.debug("Begin to upload file");

        CommonResult commonResult = new CommonResult();

        boolean flag = checkFileSize(file.getSize(), DataConstant.MAX_UPLOAD_SIZE, DataConstant.MAX_UPLOAD_UNIT);
        if (!flag) {//文件太大
            commonResult.setResult(false);
            commonResult.setCode(CommonConstant.FAILED);
            commonResult.setMessage("Upload file size exceeds limit!");
            return commonResult;
        }

        //获取上传文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String fileType = originalFilename.substring(lastIndexOf + 1);
        //文件类型判断 doc,docx,jpg,png,xls
        log.info("fileName：{}  fileType: {}", originalFilename, fileType);

        if (fileType.equals("csv") || fileType.equals("xlsx") || fileType.equals("xls")) {
            path = path + File.separator + originalFilename;
            File newFile = new File(path);
            file.transferTo(newFile);
        } else {//文件格式不符合
            commonResult.setResult(false);
            commonResult.setCode(CommonConstant.FAILED);
            commonResult.setMessage("The file format must be csv/xlsx/xls!");
            return commonResult;
        }
        commonResult.setResult(true);
        commonResult.setCode(CommonConstant.SUCCESS);
        commonResult.setMessage("File upload successfully");
        return commonResult;
    }

    /**
     * 检查文件大小，禁止超出限制
     *
     * @param len  文件大小
     * @param size 限制大小
     * @param unit
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if ("KB".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if ("MB".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if ("GB".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return (fileSize < size);
    }

    /**
     * 获取目录下所有的文件列表
     *
     * @param dirPath 文件目录路径
     * @return
     * @throws IOException
     */
    public static List<String> getFilesList(String dirPath) throws IOException {
        log.debug("begin to get file list on {}", dirPath);
        try (Stream<Path> stream = Files.list(Paths.get(dirPath))) {
            Set<String> fileSet = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());

            List<String> fileList = new ArrayList<String>();
            fileList.addAll(fileSet);

            return fileList;
        }
    }

    /**
     * 下载文件
     *
     * @param response
     * @param filePath
     * @return
     */
    public static CommonResult downloadFile(HttpServletResponse response, String filePath) {

        log.info("downloadFile : filePath : {}" ,filePath);

        CommonResult commonResult = new CommonResult();
        if (CommonUtil.isEmpty(filePath)) {
            commonResult.setCode(1);
            commonResult.setResult(false);
            commonResult.setMessage("Invalid path parameter");
            return commonResult;
        }

        //设置文件路径
        File file = new File(filePath);
        String fileName = file.getName();
        long fileLength = file.length();

        if (file.exists()) {
//                response.setContentType("application/force-download");// 设置强制下载不打开
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-Length", String.valueOf(fileLength));
//                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(), StandardCharsets.UTF_8));

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                 BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

                byte[] buffer = new byte[2048];
                int bytesRead;
                while (-1 != (bytesRead = bis.read(buffer, 0, buffer.length))) {
                    bos.write(buffer, 0, bytesRead);
                }
                commonResult.setCode(0);
                commonResult.setResult(true);
                commonResult.setMessage("Download successful");
                return commonResult;

            } catch (Exception e) {
                log.error("Error downloading file");
                log.error("ERROR:",e);
            }
            commonResult.setCode(1);
            commonResult.setResult(false);
            commonResult.setMessage("Download failed");
            return commonResult;
        }
        commonResult.setCode(1);
        commonResult.setResult(false);
        commonResult.setMessage("file does not exist");
        return commonResult;
    }
}
