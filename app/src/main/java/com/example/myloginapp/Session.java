package com.example.myloginapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void set(int userID, String email, String password) {
        prefs.edit().putInt("userID", userID).commit();
        prefs.edit().putString("email", email).commit();
        prefs.edit().putString("password", password).commit();
    }

    public void clear() {
        prefs.edit().putInt("userID", -1).commit();
        prefs.edit().putString("email", "").commit();
        prefs.edit().putString("password", "").commit();
    }

    public int getUserID() {
        int userID = prefs.getInt("userID",-1);
        return userID;
    }

    public String getEmail() {
        String username = prefs.getString("email","");
        return username;
    }

    public String getPassword() {
        String password = prefs.getString("password","");
        return password;
    }
}