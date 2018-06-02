package com.nsm.mvc.controller;

import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Created by Administrator on 2018/5/27.
 */
public class ErrorHandler {
    private Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */

    @ResponseStatus(reason = "Internal Server Error",value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ErrorCode errorHandler(Exception ex) {
        ErrorCode errorCode = ErrorCode.INNER_ERROR;
        logger.error(ErrorCode.INNER_ERROR.getMsg(), ex);
        return errorCode;
    }

    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ErrorCode businessErrorHandler(BusinessException ex) {
        logger.warn(ex.getErrorCode().getMsg());
        return ex.getErrorCode();
    }

}
