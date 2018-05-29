package com.nsm.commom.utils;

/**
 * Created by Administrator on 2018/5/27.
 */
public class IdUtils {
    public static long nextId(){
        long id = System.currentTimeMillis();
        return id;
    }
}
