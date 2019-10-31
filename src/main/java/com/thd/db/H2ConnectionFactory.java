package com.thd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionFactory {

    private  static Object mLock = new Object();

    private static final String JDBC_URL = "jdbc:h2:./thd";
    private static final String USER = "thd";
    private static final String PASSWORD = "thd";
    private static final String DRIVER_CLASS = "org.h2.Driver";

    private static Connection conn;

    public static Connection getConnection(){

        if ( conn==null ){
            synchronized (mLock) {
                if ( conn==null ){
                    try {
                        Class.forName(DRIVER_CLASS);
                        conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return conn;
    }
}
