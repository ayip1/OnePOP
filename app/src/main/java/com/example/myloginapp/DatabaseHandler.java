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

            if (user_id>0) return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
