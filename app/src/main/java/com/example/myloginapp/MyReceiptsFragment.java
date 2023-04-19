package com.example.myloginapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myloginapp.Data.Session;
import com.example.myloginapp.Database.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReceiptsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyReceiptsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Session session;
    private int userID;


    public MyReceiptsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyReceiptsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyReceiptsFragment newInstance(String param1, String param2) {
        MyReceiptsFragment fragment = new MyReceiptsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        session = new Session(getContext());
        userID = session.getUserID();

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_receipts, container, false);

        TableLayout tableLayout = rootView.findViewById(R.id.table_receipt);
        tableLayout.setWeightSum(4); // Set the weight sum to the total weight of all columns


        ResultSet rs = DatabaseHandler.getReceipts(userID);



        while (true) {
            try {
                while (rs.next()) {
                    TableRow row = new TableRow(getContext());
                    int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);

                    TextView upload = new TextView(getContext());
                    upload.setText(rs.getString(4));
                    upload.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    upload.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    upload.setGravity(Gravity.CENTER_HORIZONTAL);
                    upload.setLayoutParams(layoutParams);
                    row.addView(upload);

                    TextView category = new TextView(getContext());
                    category.setText(rs.getString(12));
                    category.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    category.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    category.setGravity(Gravity.CENTER_HORIZONTAL);
                    category.setLayoutParams(layoutParams);

                    row.addView(category);

                    TextView store = new TextView(getContext());
                    store.setLayoutParams(layoutParams);
                    store.setText(rs.getString(9));
                    store.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    store.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    store.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.addView(store);

                    TextView total = new TextView(getContext());
                    total.setLayoutParams(layoutParams);
                    total.setText(rs.getString(5));
                    total.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    total.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    total.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.addView(total);

                    TextView purchase = new TextView(getContext());
                    purchase.setLayoutParams(layoutParams);
                    purchase.setText(rs.getString(6));
                    purchase.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    purchase.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    purchase.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.addView(purchase);

                    TextView payment = new TextView(getContext());
                    payment.setLayoutParams(layoutParams);
                    payment.setText(rs.getString(11));
                    payment.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                    payment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    payment.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.addView(payment);

                    tableLayout.addView(row);
                }
                rs.close();
                break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return rootView;
    }
}