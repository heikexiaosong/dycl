package com.thd.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;
import java.util.List;

public class Main {


    private static void init(Connection conn) throws SQLException {

        QueryRunner runner = new QueryRunner();

        int p1 = runner.execute(conn, "CREATE TABLE IF NOT EXISTS PRODUCT(id int AUTO_INCREMENT PRIMARY KEY NOT NULL, code VARCHAR2(32), name VARCHAR(300))");

        int p2 = runner.execute(conn, "CREATE TABLE IF NOT EXISTS PROCESS(" +
                "id int AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
                "code VARCHAR2(32), " +
                "station VARCHAR2(8), " +
                "assembly VARCHAR2(64), " +
                "part VARCHAR2(64), " +
                "title VARCHAR2(100), " +
                "qty int, " +
                "tag VARCHAR2(50))");

        System.out.println("P1: " + p1 + "; P2: " + p2);

    }


    private static int insert(Connection conn, Product product){

        String sql = "INSERT INTO PRODUCT(code, name) VALUES(?, ?)";

        QueryRunner runner = new QueryRunner();
        try {
            Integer id = runner.insert(conn, sql, new ScalarHandler<Integer>(), product.getCode(), product.getName());
            if ( id!=null ){
                product.setId(id);
            }
            System.out.println(id);
            return id.intValue();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int insert(Connection conn, Process process){

        String sql = "INSERT INTO PROCESS(code, station, assembly, part, title, qty, tag) VALUES(?, ?, ?, ?, ?, ?, ?) ";

        QueryRunner runner = new QueryRunner();
        try {
            Integer id = runner.insert(conn, sql, new ScalarHandler<Integer>(), process.getCode(), process.getStation(), process.getAssembly(), process.getPart(), process.getTitle(), process.getQty(), process.getTag());
            if ( id!=null ){
                process.setId(id);
            }
            System.out.println(id);
            return id.intValue();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }



    public static void main(String[] args) throws Exception {

        Connection conn = H2ConnectionFactory.getConnection();

        init(conn);

        Product product = new Product();
        product.setCode("1111");
        product.setName("测试1");
        insert(conn, product);

        QueryRunner runner = new QueryRunner();


        String sql = "select * from PRODUCT";
        List<Product> products = runner.query(conn, sql, new BeanListHandler<Product>(Product.class));
        for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i));
        }


        Process process = new Process();

        process.setCode("TX0012-2234");
        process.setStation("1");
        process.setAssembly("'RKM5317722G11'");
        process.setPart("'5317722D10'");
        process.setTitle("1号导轨组件，左");
        process.setQty(1);
        process.setTag("'上部'");
        insert(conn, process);

        sql = "select * from PROCESS";
        List<Process> processes = runner.query(conn, sql, new BeanListHandler<Process>(Process.class));
        for (int i = 0; i < processes.size(); i++) {
            System.out.println(processes.get(i));
        }

        conn.close();
    }
}
