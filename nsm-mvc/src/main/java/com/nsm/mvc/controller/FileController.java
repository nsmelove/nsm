package com.nsm.mvc.controller;

import com.nsm.common.fastdfs.FastdfsClient;
import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.apache.commons.fileupload.FileUploadBase;
import org.csource.common.MyException;
import org.csource.fastdfs.DownloadStream;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.UploadStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nieshuming on 2018/6/8.
 */
@RestControllerAdvice
@RequestMapping("/file")
public class FileController  extends ErrorHandler{
    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String upload(@RequestParam MultipartFile file){
        StorageClient1 storageClient = FastdfsClient.getStorageClient1();
        try {
            InputStream in = file.getInputStream();
            int file_ext_sp_inx = file.getName().lastIndexOf(".");
            String file_ext_name = file_ext_sp_inx > 0 ? file.getName().substring(file_ext_sp_inx + 1) : null;
            String[] results = storageClient.upload_file(null, file.getSize(), new UploadStream(in, file.getSize()), file_ext_name, null);
            return results[1];
        } catch (IOException e) {
            logger.error("get upload input stream error", e);
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (MyException e) {
            logger.error("upload file to fastdfs error", e);
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(value = "/download/{fid}")
    @ResponseBody
    public ResponseEntity download(@PathVariable String fid){
        StorageClient1 storageClient = FastdfsClient.getStorageClient1();
        try {
            //response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            //ServletOutputStream out =response.getOutputStream();
            //storageClient.downL

            storageClient.download_file(null, fid, new DownloadStream(null));
            return null;
        } catch (IOException e) {
            logger.error("get download output stream error", e);
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.BAD_REQUEST));
        } catch (MyException e) {
            logger.error("upload form fastdfs error", e);
            throw new BusinessException(ErrorCode.fromHttpStatus(HttpStatus.NOT_FOUND));
        }

    }
}
