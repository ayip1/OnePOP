package com.example.myloginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myloginapp.Data.Receipt;
import com.example.myloginapp.Data.Session;
import com.example.myloginapp.Database.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ReceiptConfirmation extends AppCompatActivity {
    private Session session;
    EditText updateCategory;
    EditText updateDescription;
    EditText updateDate;
    EditText updateTotalPrice;
    EditText updatePayment;
    Button confirmReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_receipt_info);

        updateCategory = findViewById(R.id.category);
        updateDescription = findViewById(R.id.description);
        updateDate = findViewById(R.id.date);
        updateTotalPrice = findViewById(R.id.total_price);
        updatePayment = findViewById(R.id.payment_type);
        confirmReceipt = findViewById(R.id.save_receipt_details);

        String JSONString = getIntent().getStringExtra("JSONString");

        try {
            JSONObject receiptJSON = new JSONObject(JSONString);
            autofillForm(receiptJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        confirmReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isValidSession(session)) //Invalid Session, return to login
                    startActivity(new Intent(getApplicationContext(), Login.class));

                int userID = session.getUserID();

                Calendar calendar = Calendar.getInstance();
                int uploadYear = calendar.get(Calendar.YEAR);
                int uploadMonth = calendar.get(Calendar.MONTH) + 1;
                int uploadDay = calendar.get(Calendar.DAY_OF_MONTH);
                String currentDate = uploadYear + "-" + uploadMonth + "-" + uploadDay;

                String category = updateCategory.getText().toString();
                String store = updateDescription.getText().toString();
                String purchaseDate = updateDate.getText().toString();
                double total = Double.parseDouble(updateTotalPrice.getText().toString());
                String payMethod = updatePayment.getText().toString();

                Receipt receipt = new Receipt.Builder()
                        .setMetaData(userID, currentDate)
                        .setReceiptData(total, purchaseDate, null, payMethod, category)
                        .setStoreData(store, null, null)
                        .setBlob(null)
                        .build();

                int folderID = getIntent().getIntExtra("folderID", -1);
                DatabaseHandler.insertReceipt(folderID, receipt);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void autofillForm(JSONObject j) throws JSONException {
        if(j.has("category")) updateCategory.setText(j.getString("category"));
        if(j.has("date")) updateDate.setText(j.getString("date"));
        if(j.has("payment")) updatePayment.setText(j.getJSONObject("payment").getString("display_name"));
        if(j.has("subtotal") && j.has("tax")) {
            double totalPrice;
            if(!j.isNull("subtotal") && !j.isNull("tax")) {
                totalPrice = j.getDouble("subtotal") + j.getDouble("tax");
                updateTotalPrice.setText(Double.toString(totalPrice));
            }
            else if (!j.isNull("subtotal") && j.isNull("tax")) {
                totalPrice = j.getDouble("subtotal");
                updateTotalPrice.setText(Double.toString(totalPrice));
            }

        } else if (j.has("subtotal") && !j.has("tax")) {
            if(!j.isNull("subtotal")) updateTotalPrice.setText(Double.toString(j.getDouble("subtotal")));
        }
        if(j.has("vendor")) updateDescription.setText(j.getJSONObject("vendor").getString("name"));
    }
}
