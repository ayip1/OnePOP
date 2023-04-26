package com.example.myloginapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.myloginapp.Data.Folder;
import com.example.myloginapp.Data.Group;
import com.example.myloginapp.Data.Receipt;

import java.util.List;

public class FileAdapter extends ArrayAdapter<CardView> {


    public FileAdapter(Context context, List<CardView> cards) {
        super(context, 0, cards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_file, parent, false);
        }
        TextView filename = convertView.findViewById(R.id.file_name);
        ImageView thumbnail = convertView.findViewById(R.id.file_img);

        Object tag = cardView.getTag();
        if (tag instanceof Receipt) {
            Receipt receipt = (Receipt) tag;
            filename.setText(receipt.getStore()+" - " + receipt.getPurchaseDate());

            byte[] thumbnailData = receipt.getThumbnailData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(thumbnailData, 0, thumbnailData.length);
            thumbnail.setImageBitmap(bitmap);
            thumbnail.setPadding(10,0,10,0);

        } else if (tag instanceof Folder) {
            Folder folder = (Folder) tag;
            filename.setText(folder.getName());
        } else if (tag instanceof Group) {
            Group group = (Group) tag;
            filename.setText(group.getOrgname());
            thumbnail.setImageResource(R.drawable.baseline_group_account_24);
        }


        return convertView;
    }

}
