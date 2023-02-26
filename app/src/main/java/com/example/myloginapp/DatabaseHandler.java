package com.example.myloginapp;

import java.sql.*;

public class DatabaseHandler {

    public static boolean verifyLogin(String username, String password) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        CallableStatement cs;

        try {
            cs = con.prepareCall("{call user_auth(?,?,?) }");
            cs.setString(1, username);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.executeQuery();

            int user_id = cs.getInt(3);
            con.close();
            if (user_id>0) return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void registerUser(String firstname, String lastname, String username, String password, String email) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        try {
            String statement = "INSERT INTO user (first_name, last_name, username, password, email)"
                    + " VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(statement);
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, email);
            ps.execute();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean uniqueUsername(String username) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet result;

        try {
            String statement = "SELECT * FROM user WHERE username=?";
            ps = con.prepareCall(statement);
            ps.setString(1, username);
            result = ps.executeQuery();
            con.close();
            if (result.next()) return false;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean uniqueEmail(String email) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet result;

        try {
            String statement = "SELECT * FROM user WHERE email=?";
            ps = con.prepareCall(statement);
            ps.setString(1, email);
            result = ps.executeQuery();
            con.close();
            if (result.next()) return false;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
