package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myloginapp.Database.DatabaseHandler;
import com.example.myloginapp.Data.Session;
import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {
    EditText username, password;
    MaterialButton loginbtn;
    TextView error;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(getApplicationContext());
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (DatabaseHandler.isValidSession(session)) //Valid session already exists
            startActivity(intent);

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

                int userID = DatabaseHandler.verifyLogin(usernameVal, passwordVal);

                if (userID!=-1) {
                    session.set(userID, DatabaseHandler.getUserColumn(userID,"email"), passwordVal);
                    startActivity(intent);
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