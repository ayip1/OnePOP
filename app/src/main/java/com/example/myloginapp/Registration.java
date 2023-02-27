package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends AppCompatActivity {
    EditText firstname, lastname, username, email, password, passwordConfirm;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        passwordConfirm = (EditText) findViewById(R.id.passwordconfirm);
        signUpBtn = (Button) findViewById(R.id.signupbtn);

        firstname.addTextChangedListener(textWatcher);
        lastname.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        passwordConfirm.addTextChangedListener(textWatcher);

        signUpBtn.setEnabled(false);
        signUpBtn.setAlpha(0.5f);

        if (firstname!=null) {
            firstname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        lastname.requestFocus();
                        return true;
                    }
                    return false;
                }
            });
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstnameVal = firstname.getText().toString().trim();
                String lastnameVal = lastname.getText().toString().trim();
                String usernameVal = username.getText().toString().trim();
                String emailVal = email.getText().toString();
                String passwordVal = password.getText().toString();

                if (validCredentials()) {
                    //POST
                    DatabaseHandler.registerUser(firstnameVal, lastnameVal, usernameVal, passwordVal, emailVal);

                    //Alert Box to return to log in page
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                    builder.setCancelable(false);
                    builder.setTitle("Success!");
                    builder.setMessage("Account has been created.");
                    builder.setPositiveButton("Log In",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openLogin(view);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                    positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;;
                    positiveButton.setLayoutParams(positiveButtonLL);


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
            String text3 = firstname.getText().toString().trim();
            String text4 = lastname.getText().toString().trim();
            String text5 = email.getText().toString().trim();
            String text6 = passwordConfirm.getText().toString().trim();

            if (!text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() &&
                !text4.isEmpty() && !text5.isEmpty() && !text6.isEmpty()) {
                signUpBtn.setEnabled(true);
                signUpBtn.setAlpha(1f);
            } else {
                signUpBtn.setEnabled(false);
                signUpBtn.setAlpha(0.5f);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean validCredentials() {
        String usernameVal = username.getText().toString().trim();
        String emailVal = email.getText().toString().trim();
        String passwordVal = password.getText().toString();
        String passwordConfirmVal = passwordConfirm.getText().toString();

        if (!DatabaseHandler.uniqueUsername(usernameVal)) {
            showError(username,"Username is taken!");
            return false;
        }

        if (!usernameVal.matches("^[a-zA-Z0-9]*$")) {
            showError(username,"Username cannot contain special characters!");
            return false;
        }

        if (usernameVal.length()<3) {
            showError(username,"Username must have 3 or more characters!");
            return false;
        }

        if (!DatabaseHandler.uniqueEmail(emailVal)) {
            showError(email, "Email is taken!");
            return false;
        }

        if (!isEmailValid(emailVal)) {
            showError(email, "Invalid email address!");
            return false;
        }

        if (!passwordVal.equals(passwordConfirmVal)) {
            showError(passwordConfirm, "Password does not match!");
            return false;
        }

        if (passwordVal.length()<8) {
            showError(password, "Password must have 8 or more characters!");
            return false;
        }

        return true;

    }

    private void showError(EditText input, String msg) {
        input.setError(msg);
        input.requestFocus();
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void openLogin(View view) {
        startActivity(new Intent( this,Login.class));
    }
}