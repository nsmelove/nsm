package com.nsm.boot.socket;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by nieshuming on 2018/6/22
 */
public class SocketServer {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //Executors.newFixedThreadPool()
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 8888));
            System.out.println("server start at :" + serverSocket.getLocalPort());
            while(true) {
                Socket client = serverSocket.accept();
                executorService.execute(() ->{
                    System.out.println("accept client:" + client.getRemoteSocketAddress());
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(),"utf-8"));
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"utf8"));
                        out.write(("hello " + client.getRemoteSocketAddress()));
                        out.newLine();
                        out.flush();
                        while (true){
                            String data  = in.readLine();
                            System.out.println("receive data: " + data);
                            out.write("you say: " + data);
                            out.newLine();
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
