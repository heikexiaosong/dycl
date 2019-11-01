package com.thd.gui;

import com.thd.db.Process;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CubicleControl extends VBox {

    private boolean connected = false;

    private final int pos;

    @FXML
    private Label mName;

    @FXML
    private Label mStatus;

    @FXML
    private TableView<Process> mTable;

    @FXML
    private TableColumn<Process, String> mColumnPart;

    @FXML
    private TableColumn<Process, String> mColumnTitle;

    @FXML
    private TableColumn<Process, String> mColumnStatus;

    public CubicleControl(int pos, String name) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cubicle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            mColumnPart.setCellValueFactory(new PropertyValueFactory<Process, String>("part"));
            mColumnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            mColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            mName.setText(name);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.pos = pos;
    }

    public boolean accept(int pos){
        return this.pos == pos;
    }

    public boolean onClosed(){
        this.connected = false;
        mStatus.setStyle("-fx-background-color: rgba(127,127,127,0.46);");
        mStatus.setText("装配中...");
        return connected;
    }

    public boolean onConnected(){
        this.connected = true;
        mStatus.setStyle("-fx-background-color: rgba(0,255,0,0.46);");
        mStatus.setText("装配完成");
        return connected;
    }

    public void setDatas(List<Process> datas) {
        mTable.getItems().clear();
        mTable.getItems().addAll(datas);
        mTable.refresh();
    }

    public void insert(Process e) {
        mTable.getItems().add(e);
        mTable.refresh();
    }

    public void onScan(String code) {
        if ( code==null || code.trim().length()==0 ){
            return;
        }

        boolean complete = true;
        for (Process process : mTable.getItems()) {
            if ( code.trim().equalsIgnoreCase(process.getPart()) ){
                process.setStatus("已扫描");
                process.setSuccess(true);
            }
            complete &= process.isSuccess();
        }
        mTable.refresh();

        if ( complete ){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    onConnected();
                }
            });
        }
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return mStatus.textProperty();
    }

}
