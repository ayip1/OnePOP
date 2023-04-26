package com.example.myloginapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myloginapp.Data.Folder;
import com.example.myloginapp.Data.Receipt;
import com.example.myloginapp.Data.Session;
import com.example.myloginapp.Database.DatabaseHandler;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReceiptsFragment} factory method to
 * create an instance of this fragment.
 */
public class MyReceiptsFragment extends Fragment {

    MainActivity mainActivity;
    private GridView grid;

    private List<CardView> cardArr = new ArrayList<>();
    private int userID, folderID;
    private ResultSet foldersRs, receiptsRs;
    View rootView;
    public MyReceiptsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        folderID = mainActivity.currentFolderID;
        userID = mainActivity.userID;
        receiptsRs = DatabaseHandler.getReceipts(userID, folderID);
        foldersRs = DatabaseHandler.getChildFolders(folderID);

            if (!DatabaseHandler.isValidSession(mainActivity.session)) //Invalid Session, return to login
                startActivity(new Intent(getContext(), Login.class));

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_receipts, container, false);
        rootView.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        grid = rootView.findViewById(R.id.my_receipts_grid);
        populateFragment();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CardView cardPos = cardArr.get(i);
                Object tag = cardPos.getTag();

                if (tag instanceof Receipt) {
                    Receipt receipt = (Receipt) tag;
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.receipt_dialog);
                    ImageView preview = dialog.findViewById(R.id.receipt_preview);
                    TextView uploadDate = dialog.findViewById(R.id.upload_date);
                    TextView store = dialog.findViewById(R.id.store);
                    TextView category = dialog.findViewById(R.id.category);
                    TextView total = dialog.findViewById(R.id.total);
                    TextView payment = dialog.findViewById(R.id.payment_type);
                    TextView purchaseDate = dialog.findViewById(R.id.purchase_date);
                    Button btnFull = dialog.findViewById(R.id.btn_full);
                    Button btnClose = dialog.findViewById(R.id.btn_close);

                    byte[] previewData = receipt.getThumbnailData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(previewData, 0, previewData.length);
                    preview.setImageBitmap(bitmap);

                    uploadDate.setText(receipt.getUploadDate());
                    store.setText(receipt.getStore());
                    category.setText(receipt.getCategory());
                    total.setText(""+receipt.getTotal());
                    payment.setText(receipt.getPayment());
                    purchaseDate.setText(receipt.getPurchaseDate());

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnFull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(),  Fullview.class);
                            byte[] thumbnailData = receipt.getImageData();

                            i.putExtra("imageData", thumbnailData);

                            startActivity(i);
                        }
                    });

                    dialog.show();
                } else {
                    rootView.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    Folder folder = (Folder) tag;
                    mainActivity.currentFolderID =folder.getID();
                    mainActivity.parentFolderID = folder.getParentID();
                    mainActivity.replaceFragment(new MyReceiptsFragment());
                }


            }
        });

        return rootView;
    }

    private void populateFragment() {

        mainActivity.toolbar.setTitle(mainActivity.getHeader());

        try {
            while (foldersRs.next()) {
                int folderID = foldersRs.getInt(1);
                int parentID = foldersRs.getInt(5);
                String folderName = foldersRs.getString(2);
                Folder folderData = new Folder(folderID, folderName, parentID);

                CardView folder = new CardView(getContext());
                folder.setTag(folderData);

                cardArr.add(folder);
            }
            foldersRs.close();

            while (receiptsRs.next()) {
                String uploadDate = receiptsRs.getString(4);
                double total = receiptsRs.getDouble(5);
                String purchaseDate = receiptsRs.getString(6);
                String store = receiptsRs.getString(9);
                String payment = receiptsRs.getString(11);
                String category = receiptsRs.getString(12);
                byte[] fullImgData = receiptsRs.getBytes(13);
                byte[] thumbnailData = receiptsRs.getBytes(14);

                Receipt receiptData = new Receipt.Builder()
                        .setMetaData(userID, uploadDate)
                        .setReceiptData(total, purchaseDate, null, payment, category)
                        .setStoreData(store, null, null)
                        .setImageData(fullImgData, thumbnailData)
                        .build();

                CardView receipt = new CardView(getContext());
                receipt.setTag(receiptData);
                cardArr.add(receipt);
            }
            receiptsRs.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        FileAdapter adapter = new FileAdapter(getContext(), cardArr);
        grid.setAdapter(adapter);


    }



    //Listener
    //If folder -> new fragment
    //If receipt -> new activity

}