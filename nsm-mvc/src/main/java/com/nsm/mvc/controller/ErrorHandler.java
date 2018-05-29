package com.nsm.mvc.controller;

import com.nsm.mvc.exception.BusinessException;
import com.nsm.mvc.exception.ErrorCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Created by Administrator on 2018/5/27.
 */
public class ErrorHandler {
    private Log logger = LogFactory.getLog(ErrorHandler.class);
    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    @ResponseBody
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
