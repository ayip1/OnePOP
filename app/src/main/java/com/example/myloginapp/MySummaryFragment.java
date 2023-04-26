package com.example.myloginapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myloginapp.Database.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySummaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int userID;

    MainActivity mainAct;

    public MySummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MySummaryFragment newInstance(String param1, String param2) {
        MySummaryFragment fragment = new MySummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainAct = (MainActivity) getActivity();
        userID = mainAct.userID;
        if (!DatabaseHandler.isValidSession(mainAct.session)) //Invalid Session, return to login
            startActivity(new Intent(getContext(), Login.class));
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_summary, container, false);
        TextView totalspending = rootView.findViewById(R.id.totalspending);
        TextView totalreceipts = rootView.findViewById(R.id.totalreceipts);
        ResultSet summary = DatabaseHandler.getSummary(userID);
        try {
            if(summary.next()){
                totalspending.setText(totalspending.getText().toString() + " $" + summary.getInt(2));
                totalreceipts.setText(totalreceipts.getText().toString() + summary.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rootView;
    }
}
