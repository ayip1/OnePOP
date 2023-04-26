package com.example.myloginapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        updateCategory = findViewById(R.id.category);
        updateDescription = findViewById(R.id.description);
        updateDate = findViewById(R.id.date);
        updateTotalPrice = findViewById(R.id.total_price);
        updatePayment = findViewById(R.id.payment_type);
        confirmReceipt = findViewById(R.id.save_receipt_details);

        //byte[] imageData = getIntent().getByteArrayExtra("imageData");
        byte[] imageData = null;
        byte[] thumbnailData = null;
        String JSONString = getIntent().getStringExtra("JSONString");

        try {
            JSONObject receiptJSON = new JSONObject(JSONString);
            autofillForm(receiptJSON);
            imageData = grabFullImage(receiptJSON);
            thumbnailData = grabThumbnail(receiptJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] finalImageData = imageData;
        byte[] finalThumbnailData = thumbnailData;
        confirmReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

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
                        .setImageData(finalImageData, finalThumbnailData)
                        .build();

                int folderID = getIntent().getIntExtra("folderID", -1);
                DatabaseHandler.insertReceipt(folderID, receipt);

                setResult(555);
                finish();

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

    private byte[] grabThumbnail(JSONObject j) throws JSONException, IOException {
        String imageUrlString = j.getString("img_thumbnail_url");
        URL imageUrl = new URL(imageUrlString);
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();

        Bitmap bitmap = BitmapFactory.decodeStream(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        return imageData;
    }

    private byte[] grabFullImage(JSONObject j) throws JSONException, IOException {
        String imageUrlString = j.getString("img_url");
        URL imageUrl = new URL(imageUrlString);
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();

        Bitmap bitmap = BitmapFactory.decodeStream(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        do {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 5;
        } while (baos.size() > 200000 && quality > 0); // reduce quality until file size is less than 1MB

        byte[] imageData = baos.toByteArray();

        return imageData;
    }



}
