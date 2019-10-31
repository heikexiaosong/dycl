package com.thd.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    private final int port;

    private final String label;

    private ServerSocket serverSocket;

    public TcpServer(int port, String label) {
        this.port = port;
        this.label = label;
    }

    public static void main(String[] args) throws IOException {
       new TcpServer(9000, "测试位").start();
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
