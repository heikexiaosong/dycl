package com.thd.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

        private final ExecutorService executorService;
        private IHandler handler;
        private final int port;
        private final String label;
        private ServerSocket serverSocket;


        public TcpServer(int port, String label) {
           this(null, port, label);
        }

        public TcpServer(IHandler handler, int port, String label) {
            this.handler = handler;
            this.port = port;
            this.label = label;
            this.executorService = Executors.newFixedThreadPool(8);
        }

        public void setHandler(IHandler handler) {
            this.handler = handler;
        }

        public void start() throws IOException {
            System.out.println("[" + label + "][开始监听]Port: " + port);

            serverSocket = new ServerSocket(port);
            boolean running = true;
            while (running) {
                System.out.println("等待连接...");
                final Socket clientSocket = serverSocket.accept();    //do something with clientSocket

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("客户端[" + clientSocket.toString() + "]已连接");

                        InputStream ins = null;
                        OutputStream ous = null;
                        try {
                            ins = clientSocket.getInputStream();
                            ous = clientSocket.getOutputStream();

                            int count = 0;
                            byte[] buf = new byte[1024];
                            while ((count = ins.read(buf)) != -1) {
                                System.out.print("[Len: " + count + "]: ");
                                System.out.println(new String(buf, 0, count));
                                if ( buf[0]==-2
                                     && buf[1] == -1
                                     && buf[count-2]==13
                                     && buf[count-1]==10 ){
                                    char type = (char) buf[2];
                                    if (handler != null) {
                                        handler.handle(type, new String(buf, 3, count-5));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (ous != null) {
                                try {
                                    ous.close();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            if (ins != null) {
                                try {
                                    ins.close();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            System.out.println("客户端[" + clientSocket.toString() + "]断开连接\n");
                        }
                    }
                });

            }
        }

        public void stop() {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public static void main(String[] args) throws IOException {
        new TcpServer(10001, "").start();
    }

}
