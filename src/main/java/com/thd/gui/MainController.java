package com.thd.gui;

import com.thd.FXMLDemo;
import com.thd.OPCUtils;
import com.thd.db.Process;
import com.thd.excel.ExcelReader;
import com.thd.opc.JIVariants;
import com.thd.opc.OPCContext;
import com.thd.tcpserver.IHandler;
import com.thd.tcpserver.TcpServer;
import com.thd.utils.URLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController implements Initializable, IHandler {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static  final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static  final ScheduledExecutorService resetMonitor = Executors.newSingleThreadScheduledExecutor();

    private static  final ExecutorService finish = Executors.newFixedThreadPool(8);

    @FXML
    private GridPane gridPane;

    @FXML
    private TextArea mTextArea;

    @FXML private CheckBox mChkInterval1;

    @FXML private TextField mTextInterval1;

    @FXML private CheckBox mChkInterval2;

    @FXML private TextField mTextInterval2;

    private  final AtomicBoolean isRunning = new AtomicBoolean(false);


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

                isRunning.set(true);

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
    public void handle(int pos, String text) {

        String code = text;

        if ( text!=null && text.startsWith("http") ){
            code = URLParser.getParam(text, "itemcode");

        }


        log("[工位" + pos + "]" + code);
        if ( !isRunning.get() ) {
            log("请点击开始按钮，选择配置文件开始监测！");
        }
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
            @Override
            public void run() {

                // 线1检测
                try {
                    Item item = OPCContext.instance().readValue(OPCUtils.RESET1);
                    Object value =  JIVariants.getValue(item.read(true).getValue());
                    if ( value!=null ){
                        if ( value instanceof Boolean && ((Boolean) value).booleanValue() ){
                            log("线1重置");
                            OPCContext.instance().writeValue(OPCUtils.RESET1, 0);
                            for (CubicleControl cubicleControl : cubicleControls) {
                                if ( cubicleControl.isLine1() ){
                                    cubicleControl.reset();
                                }
                            }
                        } else if ( value instanceof Number  && ((Number) value).intValue()==1  ) {
                            log("线1重置");
                            OPCContext.instance().writeValue(OPCUtils.RESET1, 0);
                            for (CubicleControl cubicleControl : cubicleControls) {
                                if ( cubicleControl.isLine1() ){
                                    cubicleControl.reset();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 线2检测
                try {
                    Item item = OPCContext.instance().readValue(OPCUtils.RESET2);
                    Object value =  JIVariants.getValue(item.read(true).getValue());
                    if ( value!=null ){
                        if ( value instanceof Boolean && ((Boolean) value).booleanValue() ){
                            log("线2重置");
                            OPCContext.instance().writeValue(OPCUtils.RESET2, 0);
                            for (CubicleControl cubicleControl : cubicleControls) {
                                if ( cubicleControl.isLine2() ){
                                    cubicleControl.reset();
                                }
                            }
                        } else if ( value instanceof Number  && ((Number) value).intValue()==1  ) {
                            log("线2重置");
                            OPCContext.instance().writeValue(OPCUtils.RESET2, 0);
                            for (CubicleControl cubicleControl : cubicleControls) {
                                if ( cubicleControl.isLine2() ){
                                    cubicleControl.reset();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, 5000, 3000, TimeUnit.MILLISECONDS);



        // 扫描完成
        OnScanFinish scanFinish = new OnScanFinish() {
            @Override
            public void onFinish(final int pos) {

                if ( !isRunning.get() ) {
                    return;
                }

                finish.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(pos);

                        String tagid = null;
                        switch (pos) {
                            case 1:
                                tagid = OPCUtils.STATION11;
                                break;
                            case 2:
                                tagid = OPCUtils.STATION12;
                                break;
                            case 3:
                                tagid = OPCUtils.STATION13;
                                break;
                            case 4:
                                tagid = OPCUtils.STATION14;
                                break;
                            case 5:
                                tagid = OPCUtils.STATION21;
                                break;
                            case 6:
                                tagid = OPCUtils.STATION22;
                                break;
                            case 7:
                                tagid = OPCUtils.STATION23;
                                break;
                            case 8:
                                tagid = OPCUtils.STATION24;
                                break;
                            default:
                                log("位置信息[" + pos + "]无效");
                                return;
                        }

                        try {
                            OPCContext.instance().pulseSignal(tagid, 1000);
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
                CubicleControl cubicleControl = new CubicleControl(i*4 + j + 1, "[" + (i+1) + "-" + (j+1) + "]", i);
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


        // 自动运转时间 初始化
        try {
            Item value = OPCContext.instance().readValue(OPCUtils.INTERVAL1);
            mTextInterval1.setText(JIVariants.getValue(value.read(true).getValue()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            log("获取线1自动运转时间失败：" + e.getMessage());
        }

        try {
            Item value = OPCContext.instance().readValue(OPCUtils.INTERVAL2);
            mTextInterval2.setText(JIVariants.getValue(value.read(true).getValue()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            log("获取线2自动运转时间失败：" + e.getMessage());
        }

        try {
            Item value = OPCContext.instance().readValue(OPCUtils.MODE1);
            Object mode = JIVariants.getValue(value.read(true).getValue());
            if ( mode instanceof  Boolean && ((Boolean) mode).booleanValue() ){
                mChkInterval1.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("获取线2运转模式失败：" + e.getMessage());
        }


        try {
            Item value = OPCContext.instance().readValue(OPCUtils.MODE2);
            Object mode = JIVariants.getValue(value.read(true).getValue());
            if ( mode instanceof  Boolean && ((Boolean) mode).booleanValue() ){
                mChkInterval2.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("获取线2运转模式失败：" + e.getMessage());
        }


        log("启动完成");
    }

    private void log(String text){
        mTextArea.appendText(DATE_FORMAT.format(Calendar.getInstance().getTime()) + ": " + text + "\n");
    }

    public void applyInterval1(ActionEvent actionEvent) {
        try {
            String interval =  mTextInterval1.getText();
            log("线2更改时间间隔为: " + interval + "分钟");
            OPCContext.instance().writeValue(OPCUtils.INTERVAL1, Integer.parseInt(interval));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void applyInterval2(ActionEvent actionEvent) {

        try {
            String interval =  mTextInterval2.getText();
            log("线2更改时间间隔为: " + interval + "分钟");
            OPCContext.instance().writeValue(OPCUtils.INTERVAL2, Integer.parseInt(interval));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void mode1Changed(ActionEvent actionEvent) {

        if ( mChkInterval1.isSelected() ) {
            // TODO 进入自动模式
            try {
                OPCContext.instance().writeValue(OPCUtils.MODE1, 1);
                log("线1进入多工位模式");
            } catch (Exception e) {
                log("线1进入多工位模式失败： " + e.getMessage());
            }
        } else {
            // TODO 进入手动模式
            try {
                OPCContext.instance().writeValue(OPCUtils.MODE1, 0);
                log("线1进入单工位模式");
            } catch (Exception e) {
                log("线1进入单工位模式失败： " + e.getMessage());
            }
        }

    }

    public void mode2Changed(ActionEvent actionEvent) {

        if ( mChkInterval2.isSelected() ) {
            // TODO 进入自动模式
            try {
                OPCContext.instance().writeValue(OPCUtils.MODE2, 1);
                log("线2进入多工位模式");
            } catch (Exception e) {
                log("线2进入多工位模式失败： " + e.getMessage());
            }
        } else {
            // TODO 进入手动模式
            try {
                OPCContext.instance().writeValue(OPCUtils.MODE2, 0);
                log("线2进入多工位模式");
            } catch (Exception e) {
                log("线2进入多工位模式失败： " + e.getMessage());
            }
        }

    }
}
