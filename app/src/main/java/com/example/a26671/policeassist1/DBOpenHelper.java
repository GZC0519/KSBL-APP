package com.example.a26671.policeassist1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBOpenHelper {
    private static String diver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://47.99.130.80:3306/police?characterEncoding=utf-8";
    private static String user = "root";//用户名
    private static String password = "qwer";//密码

    public static Connection getConn(){
        Connection conn = null;
        try {
            Class.forName(diver);
            conn = (Connection) DriverManager.getConnection(url,user,password);//获取连接
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
