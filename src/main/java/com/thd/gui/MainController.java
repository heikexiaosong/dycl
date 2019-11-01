package com.thd.gui;

import com.thd.db.H2ConnectionFactory;
import com.thd.db.Process;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GridPane gridPane;

    private List<CubicleControl> cubicleControls = new ArrayList<>();

    public void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");

        Connection conn = H2ConnectionFactory.getConnection();
        QueryRunner runner = new QueryRunner();

        String sql = "select * from PROCESS";
        List<Process> processes = null;
        try {
            processes = runner.query(conn, sql, new BeanListHandler<Process>(Process.class));
            //cubicle1.setDatas(processes);

            for (CubicleControl cubicleControl : cubicleControls) {
                cubicleControl.setDatas(processes);
                cubicleControl.onConnected();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 4; j++) {
                CubicleControl cubicleControl = new CubicleControl();
                cubicleControls.add(cubicleControl);
                gridPane.add(cubicleControl, j, i);
            }
        }
    }
}
