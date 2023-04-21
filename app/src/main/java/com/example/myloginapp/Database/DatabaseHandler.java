package com.example.myloginapp.Database;

import android.database.Cursor;

import com.example.myloginapp.Data.Address;
import com.example.myloginapp.Data.Receipt;
import com.example.myloginapp.Data.Session;

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

    /**
     * Returns the rootFolderID of the user with the identifier
     * @param userID the identifier of the user
     * @return int of rootFolderID belonging to the identifier
     */
    public static int getUserRootFolder(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        CallableStatement cs;
        ResultSet rs;
        int rootFolderID = -1;

        try {
            cs = con.prepareCall("{call get_user_root_folder(?) }");
            cs.setInt(1, userID);
            rs = cs.executeQuery();

            if (rs.next()) rootFolderID = rs.getInt(1);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootFolderID;
    }

    public static void insertReceipt(int folderID, Receipt receipt) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;

        int userId = receipt.getUserID();
        String uploadDate = receipt.getUploadDate();

        double total = receipt.getTotal();
        String purchaseDate = receipt.getPurchaseDate();
        String barcode = receipt.getBarcode();
        String payment = receipt.getPayment();
        String category = receipt.getCategory();

        String store = receipt.getStore();
        String address = (receipt.getAddress()==null) ? "" : receipt.getAddress().toString();
        String phone = receipt.getPhone();

        byte[] imageData = receipt.getImageData();
        byte[] thumbnailData = receipt.getThumbnailData();

        try {
            String statement = "INSERT INTO receipt (user_id, folder_id, upload_date, total, purchase_date, barcode, phone, store, address, payment, category, img, thumbnail)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //String statement = "INSERT INTO receipt (user_id, folder_id, upload_date, total, purchase_date, barcode, phone, store, address, payment, category)"
            //        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(statement);
            ps.setInt(1, userId);
            ps.setInt(2, folderID);
            ps.setString(3, uploadDate);
            ps.setDouble(4, total);
            ps.setString(5, purchaseDate);
            ps.setString(6, barcode);
            ps.setString(7, phone);
            ps.setString(8, store);
            ps.setString(9, address);
            ps.setString(10, payment);
            ps.setString(11, category);
            ps.setBytes(12, imageData);
            ps.setBytes(13, thumbnailData);

            ps.execute();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ResultSet getAllReceipts(int userID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs = null;

        try {
            String statement = "SELECT * FROM receipt WHERE user_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;

    }

    public static ResultSet getReceipts(int userID, int folderID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs = null;

        try {
            String statement = "SELECT * FROM receipt WHERE user_id=? AND folder_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, userID);
            ps.setInt(2, folderID);
            rs = ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;
    }

    public static ResultSet getChildFolders(int folderID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        CallableStatement cs;
        ResultSet rs = null;

        try {
            cs = con.prepareCall("{call get_child_folders(?)}");
            cs.setInt(1, folderID);
            rs = cs.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;
    }

    public static void insertFolder(int ownerID, int parentFolderID, String folderName, boolean isOrganization) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;

        try {
            String statement = "INSERT INTO folder (folder_name, creator_id, org_id, parent_folder_id)"
                                + "VALUES (?, ?, ?, ?)";
            ps = con.prepareCall(statement);
            ps.setString(1, folderName);
            ps.setInt(4, parentFolderID);

            if (isOrganization) {
                ps.setInt(3, ownerID);
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, ownerID);
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.execute();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getParentFolderID(int folderID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs = null;
        int parentFolderID = -1;

        try {
            String statement = "SELECT parent_folder_id FROM folder WHERE folder_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, folderID);
            rs = ps.executeQuery();

            if (rs.next()) parentFolderID = rs.getInt(1);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parentFolderID;
    }

    public static String getFolderName(int folderID) {
        ApplicationDB db = new ApplicationDB();
        Connection con = db.getConnection();
        PreparedStatement ps;
        ResultSet rs = null;
        String name = "";

        try {
            String statement = "SELECT folder_name FROM folder WHERE folder_id=?";
            ps = con.prepareCall(statement);
            ps.setInt(1, folderID);
            rs = ps.executeQuery();

            if (rs.next()) name = rs.getString(1);

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }


}
