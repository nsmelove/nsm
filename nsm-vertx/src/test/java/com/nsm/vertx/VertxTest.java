package com.nsm.vertx;

/**
 * Created by nieshuming on 2018/6/22
 */
public class VertxTest {

    public static void main(String[] args){
        Object o = new Object();
        for(int i = 0 ; i< 100; i++) {
            new A(o);
        }
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();
        try {
            Thread. sleep(2000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class A{
        Object o ;

        public A(Object o){
            this.o = o;
            System.out.println("new object:" + this);
        }

        @Override
        protected void finalize() throws Throwable{
            System.out.println("finalize object:" + this);
            super.finalize();
        }
    }
}
