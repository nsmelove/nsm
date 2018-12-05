package com.nsm.mvc;

import com.nsm.common.utils.JsonUtils;
import com.nsm.bean.ErrorCode;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/19.
 */
public class StringTest {

    public static void main(String[] args) {
        System.out.println("/user/login?username=1".matches("/user/login/"));
        System.out.println("/user/alogin?username=1".contains("/user/login"));
        System.out.println(JsonUtils.toJson(ErrorCode.NO_LOGIN));
        new Thread(() ->{
            while (true){
                System.out.println("time is down");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
