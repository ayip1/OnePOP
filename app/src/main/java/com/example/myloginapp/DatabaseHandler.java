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
        ResultSet rs;
        Boolean result = true;
        try {
            String statement = "SELECT * FROM user WHERE username=?";
            ps = con.prepareCall(statement);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) result = false;

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean uniqueEmail(String email) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        Boolean result = true;
        try {
            String statement = "SELECT * FROM user WHERE email=?";
            ps = con.prepareCall(statement);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) result = false;

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getUserID(String identifier) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        int userID = -1;

        try {
            String statement = identifier.contains("@") ? "SELECT user_id FROM user WHERE email=?"
                    : "SELECT user_id FROM user WHERE username=?";
            ps = con.prepareCall(statement);
            ps.setString(1, identifier);
            rs = ps.executeQuery();

            if (rs.next()) userID = rs.getInt(1);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userID;
    }
    public static void updatePassword(int userID, String newPassword) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;

        try {
            String statement = "UPDATE user SET password=? WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setString(1, newPassword);
            ps.setInt(2, userID);
            ps.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getUsername(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        String username = "";
        try {
            String statement = "SELECT username FROM user WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) username = rs.getString(1);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return username;
    }

    public static String getPassword(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        String password = "";
        try {
            String statement = "SELECT password FROM user WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) password = rs.getString(1);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return password;
    }

    public static String getEmail(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        String email = "";
        try {
            String statement = "SELECT email FROM user WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) email = rs.getString(1);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return email;
    }
    public static String getFullName(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        String fullname = "";
        try {
            String statement = "SELECT first_name, last_name FROM user WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) fullname = rs.getString(1) + " " + rs.getString(2);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fullname;
    }

}
