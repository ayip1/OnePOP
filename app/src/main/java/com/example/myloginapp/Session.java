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

    public void setUserID(int userID) {
        prefs.edit().putInt("userID", userID).commit();

    }

    public int getUserID() {
        int userID = prefs.getInt("userID",-1);
        return userID;
    }
}