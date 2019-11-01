package com.thd.gui;

import com.thd.db.Process;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class CubicleControl extends VBox {

    private boolean connected = false;

    @FXML
    private Label mLabel;

    @FXML
    private TableView<Process> mTable;

    @FXML
    private TableColumn<Process, String> mColumnPart;

    @FXML
    private TableColumn<Process, String> mColumnTitle;

    @FXML
    private TableColumn<Process, String> mColumnStatus;

    public CubicleControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cubicle.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            mColumnPart.setCellValueFactory(new PropertyValueFactory<Process, String>("part"));
            mColumnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            mColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean onClosed(){
        this.connected = false;
        mLabel.setStyle("-fx-background-color: rgba(127,127,127,0.46);");
        mLabel.setText("未连接");
        return connected;
    }

    public boolean onConnected(){
        this.connected = true;
        mLabel.setStyle("-fx-background-color: rgba(0,255,0,0.46);");
        mLabel.setText("已连接");
        return connected;
    }

    public void setDatas(List<Process> datas) {
        mTable.getItems().clear();
        mTable.getItems().addAll(datas);
        mTable.refresh();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return mLabel.textProperty();
    }

}
