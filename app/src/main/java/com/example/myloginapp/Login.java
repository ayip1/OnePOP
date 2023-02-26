package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

public class Login extends AppCompatActivity {
    EditText username, password;
    MaterialButton loginbtn;
    TextView error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        error = (TextView) findViewById(R.id.loginerror);

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        error.setVisibility(View.GONE);
        loginbtn.setEnabled(false);
        loginbtn.setAlpha(0.5f);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usernameVal = username.getText().toString();
                String passwordVal = password.getText().toString();

                if (DatabaseHandler.verifyLogin(usernameVal, passwordVal)) {
                    startActivity(new Intent( Login.this,Homepage.class));
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text1 = username.getText().toString().trim();
            String text2 = password.getText().toString().trim();
            if (!text1.isEmpty() && !text2.isEmpty()) {
                loginbtn.setEnabled(true);
                loginbtn.setAlpha(1f);
            } else {
                loginbtn.setEnabled(false);
                loginbtn.setAlpha(0.5f);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public void openPasswordReset(View view) {
        startActivity(new Intent( this,PasswordReset.class));
    }

    public void openRegistration(View view) {
        startActivity(new Intent( this,Registration.class));
    }

}