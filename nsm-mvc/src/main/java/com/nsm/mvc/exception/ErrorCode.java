package com.nsm.mvc.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Administrator on 2018/5/27.
 */
public class ErrorCode {
    public static ErrorCode INNER_ERROR = new ErrorCode(500, "系统内部错误");
    public static ErrorCode NOT_FOUND = new ErrorCode(404, "没有找到数据");

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
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
