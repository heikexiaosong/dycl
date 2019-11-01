package com.thd.tcpserver;

import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

    private final int port;

    private final String label;

    private ServerSocket serverSocket;

    public TcpServer(int port, String label) {
        this.port = port;
        this.label = label;
    }

    public static void main(String[] args) throws IOException {

        final ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 8; i++) {
            final int offset = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        new TcpServer(10001+offset, "测试位-" + offset).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void start() throws IOException {
        System.out.println("[" + label + "][开始监听]Port: " + port);

        serverSocket = new ServerSocket(port);
        boolean running = true;
        while ( running ){
            System.out.println("等待连接...");
            Socket clientSocket = serverSocket.accept();    //do something with clientSocket
            System.out.println("客户端[" + clientSocket.toString() + "]已连接");

            InputStream ins = null;
            OutputStream ous = null;
            try {
                ins = clientSocket.getInputStream();
                ous = clientSocket.getOutputStream();

                int count = 0;
                byte[] buf = new byte[1024];
                while ( (count = ins.read(buf)) != -1 ){

                    System.out.print("[Len: " + count + "]: ");
                    for (int i = 0; i < count; i++) {
                        System.out.print(buf[i] + " ");
                    }
                    System.out.println();
                }
            } finally {
                if ( ous!=null ){
                    try {
                        ous.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if ( ins!=null ){
                    try {
                        ins.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                System.out.println("客户端[" + clientSocket.toString() + "]断开连接\n");
            }

        }
    }

    public void stop(){
        if ( serverSocket!=null ){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
