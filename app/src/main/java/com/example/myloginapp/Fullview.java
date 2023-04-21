package com.example.myloginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Base64;

public class Fullview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);
        ImageView img = findViewById(R.id.img_full);

        byte[] imageData = getIntent().getByteArrayExtra("imageData");
        
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        img.setImageBitmap(bitmap);
    }
}