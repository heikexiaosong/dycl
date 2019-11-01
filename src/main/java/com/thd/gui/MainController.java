package com.thd.gui;

import com.thd.FXMLDemo;
import com.thd.db.H2ConnectionFactory;
import com.thd.db.Process;
import com.thd.excel.ExcelReader;
import com.thd.excel.ExcelUtils;
import com.thd.tcpserver.IHandler;
import com.thd.tcpserver.TcpServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable, IHandler {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static  final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    private GridPane gridPane;

    @FXML
    private TextArea mTextArea;

    private List<CubicleControl> cubicleControls = new ArrayList<>();

    public void start(ActionEvent event) {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择工序物料配置文件");

        File file = fileChooser.showOpenDialog(null);
        if ( file!=null ){
            log("打开文件" + file.getAbsolutePath());

            try {
                List<Process> processes =  ExcelReader.load(file);

                for (CubicleControl cubicleControl : cubicleControls) {
                    cubicleControl.setDatas(new ArrayList<>());
                }

                for (Process process : processes) {
                    System.out.println(process);

                    for (CubicleControl cubicleControl : cubicleControls) {
                        if ( cubicleControl.accept(process.getStation()) ){
                            cubicleControl.insert(process);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void restart(ActionEvent event) {
        System.out.println("You clicked me!");

        Connection conn = H2ConnectionFactory.getConnection();
        QueryRunner runner = new QueryRunner();

        String sql = "select * from PROCESS";
        List<Process> processes = null;
        try {
            processes = runner.query(conn, sql, new BeanListHandler<Process>(Process.class));

            for (CubicleControl cubicleControl : cubicleControls) {
                cubicleControl.setDatas(processes);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handle(int pos, String code) {
        log("[工位" + pos + "]" + code);
        for (CubicleControl cubicleControl : cubicleControls) {
           if ( cubicleControl.accept(pos) ){
               cubicleControl.onScan(code);
               return;
           }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 4; j++) {
                CubicleControl cubicleControl = new CubicleControl(i*2 + j + 1, "[" + (i+1) + "-" + (j+1) + "]");
                cubicleControls.add(cubicleControl);
                gridPane.add(cubicleControl, j, i);
            }
        }

        TcpServer tcpServer = new TcpServer(10001, "测试位-");
        tcpServer.setHandler(this);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("11111");
                    FXMLDemo.CDL.await();
                    System.out.println("22222");
                    executorService.shutdownNow();
                    if ( !executorService.isTerminated() ){
                        executorService.shutdown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        log("启动完成");
    }

    private void log(String text){
        mTextArea.appendText(DATE_FORMAT.format(Calendar.getInstance().getTime()) + ": " + text + "\n");
    }
}
