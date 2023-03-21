package com.example.myloginapp.Database;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class ApplicationDB {
    Connection connection;
    String username, password, endpoint, port, database;
    public Connection getConnection() {
        //Create a connection string
        endpoint = "database-1.c3xlj8okoqmz.us-east-2.rds.amazonaws.com";
        port = "3306";
        database = "onepop";
        username = "admin";
        password = "password";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionUrl = "jdbc:mysql://"+endpoint+":"+port+"/"+database;
        try {
            //Load JDBC driver & Create a connection to the DB
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionUrl, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
