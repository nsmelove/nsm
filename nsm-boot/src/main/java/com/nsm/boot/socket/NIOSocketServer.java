package com.nsm.boot.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by nieshuming on 2018/6/22.
 */
public class NIOSocketServer {
    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server start at " + serverSocketChannel.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("server start failed !");
            return;
        }

        while(true){
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> i = keys.iterator();
                while ( i.hasNext() ) {
                    SelectionKey key = i.next();
                    if(!key.isValid()){
                        continue;
                    }
                    if(key.isAcceptable()) {
                        SocketChannel channel = serverSocketChannel.accept();
                        if(channel==null){
                            continue;
                        }
                        channel.configureBlocking( false );
                        System.out.println("receive client:" + channel.getRemoteAddress());
                        channel.write(ByteBuffer.wrap(("welcome " + channel.getRemoteAddress()).getBytes()));
                        channel.register(selector, SelectionKey.OP_READ, channel);
                    }
                    if(key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.attachment();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        //TODO
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
