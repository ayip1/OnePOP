package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

public class Login extends AppCompatActivity {
    TextView signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        signin = (TextView) findViewById(R.id.signin);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usernameVal = username.getText().toString();
                String passwordVal = password.getText().toString();

                if (DatabaseHandler.verifyLogin(usernameVal, passwordVal)) {
                    startActivity(new Intent( Login.this,Homepage.class));
                } else {
                    Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void openPasswordReset(View view) {
        startActivity(new Intent( this,PasswordReset.class));
    }

    public void openRegistration(View view) {
        startActivity(new Intent( this,Registration.class));
    }

}