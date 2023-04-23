package com.example.myloginapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGroupsFragment extends Fragment {
    View rootView;


    public MyGroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        rootView.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return rootView;
    }

}