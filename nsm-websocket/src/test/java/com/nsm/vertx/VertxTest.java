package com.nsm.vertx;

import com.google.common.collect.Maps;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by nieshuming on 2018/6/22
 */
public class VertxTest {

    public <T> void executeAsync(Supplier<T> supplier, Consumer<T> consumer){
        new Thread(new Runnable() {
            @Override
            public void run() {
                T result = supplier.get();
                consumer.accept(result);
            }
        });
    }

    public <T> void executeAsync(Consumer<Consumer<T>> run, Consumer<T> handler){
        new Thread(() -> {
            Consumer<T> cons = t ->{
                handler.accept(t);
            };
            run.accept(cons);
        }).start();
    }

    public static void main(String[] args){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        VertxOptions vertxOptions = new VertxOptions();
        //vertxOptions.setEventLoopPoolSize(3);
        //vertxOptions.setWorkerPoolSize(3);
        Vertx.clusteredVertx(vertxOptions, res -> {
            System.out.println("clustered vertx success ? " + res.succeeded());
            countDownLatch.countDown();
            if (res.succeeded()) {
                CountDownLatch countDownLatch1 = new CountDownLatch(1);
                res.result().sharedData().getAsyncMap("test", mapRes -> {
                    System.out.println("AsyncMap success ? " + mapRes.succeeded());
                    countDownLatch1.countDown();
                });
                try {
                    Thread.getAllStackTraces().keySet().forEach(System.out::println);
                    //将会一直阻塞下去
                    countDownLatch1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("AsyncMap finished");
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("clustered vertx finished");
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
