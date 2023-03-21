package com.example.myloginapp.Data;

import java.sql.*;

public class DatabaseHandler {

    /**
     * Checks if a given user login is valid
     * @param username of the user to check
     * @param password of the user to check
     * @return true if valid and false, otherwise
     */
    public static int verifyLogin(String username, String password) {
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

            return user_id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Checks if a given user session is valid by checking if the session
     * email and password are consistent with the database.
     * @param session of the user
     * @return true if user email or password are consistent and false, otherwise.
     */
    public static boolean isValidSession(Session session) {
        int sessionUserID = session.getUserID();
        if (sessionUserID==-1) //Invalid userID
            return false;


        String sessionEmail = session.getEmail();
        String sessionPwd = session.getPassword();
        String dbEmail = getUserColumn(sessionUserID, "email");
        String dbPwd = getUserColumn(sessionUserID,"password");

        if (!dbEmail.equals(sessionEmail) || !dbPwd.equals(sessionPwd))
            return false;


        return true;
    }

    /**
     * Updates a column of a user with the specified ID
     * @param userID the ID of the user to update
     * @param columnName the name of the column to update (must be a valid column name of the "user" table)
     * @param columnValue the new value for the column
     * @throws IllegalArgumentException if an invalid columnName is provided
     */
    public static void updateUserColumn(int userID, String columnName, String columnValue) throws IllegalArgumentException {
        String[] validColumns = {"email", "first_name", "last_name", "username", "password"};
        boolean isValidColumn = false;
        for (String column : validColumns) {
            if (columnName.equals(column)) {
                isValidColumn = true;
                break;
            }
        }
        if (!isValidColumn) {
            throw new IllegalArgumentException("Invalid column name. Must be a valid column name of the 'user' table.");
        }

        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;

        try {
            String statement = "UPDATE user SET " + columnName + "=? WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setString(1, columnValue);
            ps.setInt(2, userID);
            ps.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns a column of a user with the specified userID
     * @param userID the ID of the user to update
     * @param columnName the name of the column to return (must be a valid column name of the "user" table)
     * @throws IllegalArgumentException if an invalid columnName is provided
     */
    public static String getUserColumn(int userID, String columnName) throws IllegalArgumentException {
        String[] validColumns = {"email", "first_name", "last_name", "username", "password"};
        boolean isValidColumn = false;
        for (String column : validColumns) {
            if (columnName.toLowerCase().equals(column)) {
                isValidColumn = true;
                break;
            }
        }
        if (!isValidColumn) {
            throw new IllegalArgumentException("Invalid column name. Must be a valid column name of the 'user' table.");
        }

        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        String result = "";

        try {
            String statement = "SELECT " + columnName + " FROM user WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next()) result = rs.getString(1);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Inserts a tuple into the user table
     * @param firstname the first name of the user to insert
     * @param lastname the last name of the user to insert
     * @param username the username of the user to insert
     * @param password the password of the user to insert
     * @param email the email of the user to insert
     */
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

    /**
     * Checks if a given username is unique within the user table
     * @param username the username of the user to check
     * @return true if unique and false, otherwise
     */
    public static boolean isUniqueUsername(String username) {
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

    /**
     * Checks if a given email is unique within the user table
     * @param email the email of the user to check
     * @return true if unique and false, otherwise
     */
    public static boolean isUniqueEmail(String email) {
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

    /**
     * Returns the userID of the user with the identifier
     * @param identifier the identifier of the user to check (email or username)
     * @return int of userID belonging to the identifier
     */
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



}
