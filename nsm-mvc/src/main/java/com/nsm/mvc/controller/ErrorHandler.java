package com.nsm.mvc.controller;

import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Created by nsm on 2018/5/27
 */
public class ErrorHandler {
    private Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    private HttpHeaders headers(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return httpHeaders;
    }

    /**
     * 全局异常捕捉处理
     * @param ex 异常
     * @return httpResponse
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorCode> errorHandler(Exception ex) {

        ErrorCode errorCode = ErrorCode.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error(errorCode.getMsg(), ex);
        return new ResponseEntity<>(errorCode, headers(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数异常
     * @param ex 异常
     * @return httpResponse
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorCode> parameterErrorHandler(MissingServletRequestParameterException ex) {
        ErrorCode errorCode = ErrorCode.fromHttpStatus(HttpStatus.BAD_REQUEST);
        logger.error(errorCode.getMsg(), ex);
        return new ResponseEntity<>(errorCode, headers(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 业务异常处理
     * @param ex 异常
     * @return httpResponse
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorCode> businessErrorHandler(BusinessException ex) {
        ErrorCode errorCode =  ex.getErrorCode();
        HttpStatus httpStatus = null;
        try {
            httpStatus = HttpStatus.valueOf(errorCode.getCode());
        }catch (IllegalArgumentException e){
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        logger.warn(errorCode.getMsg());
        return new ResponseEntity<>(errorCode, headers(), httpStatus);
    }

}
