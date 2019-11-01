package com.thd;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Properties;

public class TrayFactory {

    public static void build(final Stage stage) {
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
           try {
               BufferedImage image = ImageIO.read(TrayFactory.class.getResourceAsStream("/trayicon.png"));
               TrayIcon trayIcon = new TrayIcon(image, "thd", popupMenu);
               trayIcon.setImageAutoSize(true);

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

               final SystemTray tray = SystemTray.getSystemTray();
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

           } catch (Exception e){
                e.printStackTrace();
           }
        } else {
            System.out.println("SystemTray is not supported");
        }
    }
}
