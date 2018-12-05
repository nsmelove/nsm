package com.nsm.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nieshuming on 2018/6/22
 */
public class SpringContainer implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
        System.out.println("-------set context--------");
    }

    private static void afterInit(){
        if (context == null) {
            synchronized (ApplicationContext.class){
                if (context == null) {
                    context = new ClassPathXmlApplicationContext("classpath*:spring-*.xml");
                }
            }
        }

    }
    public static <T> T getBean(Class<T> beanClass) {
        afterInit();
        return context.getBean(beanClass);
    }

    public static<T> T getBean(String name, Class<T> beanClass) {
        afterInit();
        return context.getBean(name, beanClass);
    }
}
