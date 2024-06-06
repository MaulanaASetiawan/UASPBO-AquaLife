package com.example.gui;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbconn {
    static Connection conn;
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uaspbo", "root", "");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return conn;
    }
}
