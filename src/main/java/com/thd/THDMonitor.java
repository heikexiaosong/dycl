package com.thd;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

import com.thd.tcpserver.TcpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import javax.swing.*;

public class THDMonitor extends Application {

    private TrayIcon trayIcon;

    public static void main(String[] args) {

        Platform.setImplicitExit(false);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TcpServer(9000, "测试位").start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("ddddddddddddddddddddd");
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {

        enableTray(primaryStage);

        primaryStage.setTitle("Hello World!");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);






        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                primaryStage.hide();
                event.consume();
            }
        });




        primaryStage.show();
    }

    private void enableTray(final Stage stage){
        Properties props = System.getProperties();
        String osname = props.getProperty("os.name", "Windows").toLowerCase();
        System.out.println("当前操作系统：" + osname);
        try {
            if ( osname.contains("windows") ){
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);


        if ( SystemTray.isSupported()) {


            PopupMenu popupMenu = new PopupMenu();

            java.awt.MenuItem openItem = new java.awt.MenuItem("显示主界面");
            openItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            });
            popupMenu.add(openItem);

            java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");
            quitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SystemTray.getSystemTray().remove(trayIcon);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.hide();
                            stage.close();
                            Platform.exit();
                        }
                    });
                }
            });
            popupMenu.add(quitItem);

            try {
                final SystemTray tray = SystemTray.getSystemTray();
                BufferedImage image = ImageIO.read(THDMonitor.class.getResourceAsStream("/trayicon.png"));
                trayIcon = new TrayIcon(image, "thd", popupMenu);
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);

                trayIcon.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if( !stage.isShowing() ) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    stage.show();
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("SystemTray is not supported");
        }

    }
}
