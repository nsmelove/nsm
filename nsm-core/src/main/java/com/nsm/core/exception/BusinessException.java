package com.nsm.core.exception;

import com.nsm.bean.ErrorCode;

/**
 * Created by Administrator on 2018/5/2
 */
public class BusinessException extends RuntimeException {
    private ErrorCode errorCode;
    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(int code, String message) {
        this.errorCode = new ErrorCode(code, message);
    }
    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
