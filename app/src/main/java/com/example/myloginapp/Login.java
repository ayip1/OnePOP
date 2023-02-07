package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void openPasswordReset(View view) {
        startActivity(new Intent( this,PasswordReset.class));
    }

    public void openRegistration(View view) {
        startActivity(new Intent( this,Registration.class));
    }

    public void openHomepage(View view) {
        startActivity(new Intent( this,Homepage.class));
    }
}