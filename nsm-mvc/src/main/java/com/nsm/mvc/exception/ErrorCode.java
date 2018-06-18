package com.nsm.mvc.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

/**
 * Created by Administrator on 2018/5/27.
 */
public class ErrorCode {

    public static final ErrorCode USER_PASSWORD_WRONG = new ErrorCode(1000, "用户名或密码错误");
    public static final ErrorCode USER_FORBIDDEN = new ErrorCode(1001, "用户禁止访问");
    public static final ErrorCode NO_LOGIN = new ErrorCode(1001, "没有登录");
    public static final ErrorCode NO_AUTHENTICATION = new ErrorCode(1002, "没有权限");

    public static final ErrorCode USER_GROUP_LIMIT = new ErrorCode(1100, "用户群组达到上限");
    public static final ErrorCode USER_GROUP_LEVEL_LIMIT = new ErrorCode(1100, "用户群组层级达到上限");
    private int code;
    private String msg;
    public ErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    public static ErrorCode fromHttpStatus(HttpStatus status){
        return new ErrorCode(status.value(), status.getReasonPhrase());
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
