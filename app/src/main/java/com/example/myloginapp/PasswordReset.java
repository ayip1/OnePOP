package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class PasswordReset extends AppCompatActivity {
    EditText recoveryEmail;
    MaterialButton resetbtn;
    final String supportEmail="onepop332@gmail.com";
    final String supportEmailPass = "df@9k23Ksz";

    final String supportEmailAppPass = "gkjqsalfcfxzgqmn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        recoveryEmail = (EditText) findViewById(R.id.email);
        recoveryEmail.addTextChangedListener(textWatcher);
        resetbtn = (MaterialButton) findViewById(R.id.resetbtn);
        resetbtn.setEnabled(false);
        resetbtn.setAlpha(0.5f);

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send recovery instructions if email is registered within the database
                String recoveryEmailVal = recoveryEmail.getText().toString();

                if (!DatabaseHandler.uniqueEmail(recoveryEmailVal)) {
                    int userID = DatabaseHandler.getUserID(recoveryEmailVal);
                    String fullname = DatabaseHandler.getFullName(userID);
                    String username = DatabaseHandler.getUsername(userID);
                    String newPassword = generateRandomPassword();
                    DatabaseHandler.updatePassword(userID, newPassword);

                    String messageToSend = "Hello, " + fullname +
                            "\nPlease use the following credentials to login:\n\n" +
                            "Username: " + username +
                            "\nPassword: " + newPassword +
                            "\n\nDo not share this information with anyone else. You may update the password in account settings";

                    Properties props = System.getProperties();
                    props.put("mail.smtp.host","smtp.gmail.com");
                    props.put("mail.smtp.port","465");
                    props.put("mail.smtp.auth","true");
                    props.put("mail.smtp.starttls.enable","true");
                    props.put("mail.smtp.ssl.enable","true");

                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(supportEmail,supportEmailAppPass);
                                }
                            });
                    try {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(supportEmail));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recoveryEmailVal));
                        message.setSubject("OnePOP - Account Recovery (noreply)");
                        message.setText(messageToSend);
                        Transport.send(message);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }

                //Alert Box to return to log in page
                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordReset.this);
                builder.setCancelable(false);
                builder.setTitle("Success!");
                builder.setMessage("If an account associated with this email address exists, instructions " +
                        "containing the recovery process will be sent");
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
        });

        //Thread policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text1 = recoveryEmail.getText().toString().trim();
            if (!text1.isEmpty() && isEmailValid(text1)) {
                resetbtn.setEnabled(true);
                resetbtn.setAlpha(1f);
            } else {
                resetbtn.setEnabled(false);
                resetbtn.setAlpha(0.5f);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private static String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String num = "0123456789";
        String specialChars = "!@#$%^&*";
        String combination = upper+lower+specialChars+num;
        int len = 8;
        char[] password = new char[len];
        Random r = new Random();
        for (int i=0; i<len; i++) {
            password[i] = combination.charAt(r.nextInt(combination.length()));
        }
        return new String(password);
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void openLogin(View view) {
        startActivity(new Intent( this,Login.class));
    }
}