package com.nsm.mvc.controller;

import com.nsm.common.fastdfs.FastdfsClient;
import com.nsm.core.exception.BusinessException;
import com.nsm.bean.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.DownloadStream;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.UploadStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping
public class FileController extends ErrorHandler{
    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String upload(@RequestParam MultipartFile file){
        StorageClient1 storageClient = FastdfsClient.getStorageClient1();
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtName = StringUtils.substringAfterLast(originalFilename,".");
            InputStream in = file.getInputStream();
            String[] results = storageClient.upload_file(null, file.getSize(), new UploadStream(in, file.getSize()), fileExtName, null);
            return String.join("/", results);
        } catch (IOException e) {
            logger.error("get upload input stream error", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (MyException e) {
            logger.error("upload file to fastdfs error", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/download/**")
    public void download(HttpServletRequest req, HttpServletResponse res){
        String path = req.getServletPath();
        String fileId= path.replaceAll(".*download[/\\\\]","");
        logger.info("download fileId is {}", fileId);
        StorageClient1 storageClient = FastdfsClient.getStorageClient1();
        ServletOutputStream out = null;
        try {
            out = res.getOutputStream();
            FileInfo fileInfo = storageClient.get_file_info1(fileId);
            if(fileInfo == null) {
                logger.warn("no fileId {} in fastdfs", fileId);
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
            res.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            res.setContentLengthLong(fileInfo.getFileSize());
            int result = storageClient.download_file1(fileId, new DownloadStream(res.getOutputStream()));
            if(result != 0){
                logger.error("download result code is {}", result);
            }
        } catch (BusinessException e) {
            if(out != null) {
                res.setStatus(e.getErrorCode().getCode());
            }
        } catch (Exception e) {
            logger.error("download file error", e);
            if(out != null) {
                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
