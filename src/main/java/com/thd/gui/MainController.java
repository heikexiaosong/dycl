package com.thd.gui;

import com.thd.FXMLDemo;
import com.thd.OPCUtils;
import com.thd.db.Process;
import com.thd.excel.ExcelReader;
import com.thd.opc.JIVariants;
import com.thd.opc.OPCContext;
import com.thd.tcpserver.IHandler;
import com.thd.tcpserver.TcpServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.da.Item;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable, IHandler {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static  final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static  final ScheduledExecutorService resetMonitor = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private GridPane gridPane;

    @FXML
    private TextArea mTextArea;

    @FXML
    private CheckBox mChkInterval;

    @FXML
    private TextField mTextInterval;


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


                for (CubicleControl cubicleControl : cubicleControls) {
                    cubicleControl.check();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clear(ActionEvent event) {
        for (CubicleControl cubicleControl : cubicleControls) {
            cubicleControl.clear();
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

    /**
     * 初始化
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 定时检测重置信号
        resetMonitor.scheduleWithFixedDelay(new Runnable() {
            final  OPCContext  context = OPCContext.create();
            @Override
            public void run() {
                try {
                    Item item = context.readValue(OPCUtils.RESET);
                    Object value =  JIVariants.getValue(item.read(true).getValue());
                    if ( value instanceof Number ){
                        if ( ((Number) value).intValue() == 1 ){
                            log("获取到重置信号..");
                            context.writeValue(OPCUtils.RESET, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                log("Reset Monitor");
            }
        }, 5000, 3000, TimeUnit.MILLISECONDS);



        OnScanFinish scanFinish = new OnScanFinish() {
            final  OPCContext  context = OPCContext.create();
            @Override
            public void onFinish(int pos) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            context.pulseSignal("S7-200.D14.v20" + pos, 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log(e.getMessage());
                        }
                    }
                });


            }
        };

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                CubicleControl cubicleControl = new CubicleControl(i*4 + j + 1, "[" + (i+1) + "-" + (j+1) + "]");
                cubicleControl.setScanFinish(scanFinish);
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

    public void applyInterval(ActionEvent actionEvent) {

        String tagid = "S7-200.D14.INTERVAL";
        try {
            String interval =  mTextInterval.getText();
            log("更改时间间隔为: " + interval + "分钟");
            final  OPCContext  context = OPCContext.create();
            context.writeValue(tagid, Integer.parseInt(interval));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void modeChanged(ActionEvent actionEvent) {

        if ( mChkInterval.isSelected() ) {
            // TODO 进入自动模式
            log("进入自动模式");
        } else {
            // TODO 进入手动模式
            log("进入手动模式");
        }

    }
}
