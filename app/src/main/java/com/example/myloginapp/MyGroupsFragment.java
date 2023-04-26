package com.example.myloginapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.myloginapp.Data.Folder;
import com.example.myloginapp.Data.Group;
import com.example.myloginapp.Data.Receipt;
import com.example.myloginapp.Database.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGroupsFragment extends Fragment {
    //Rahuls changes start

    MainActivity mainActivity;

    private GridView grid;

    private List<CardView> cardArr = new ArrayList<>();
    private int userID, orgID;
    private String orgname, rolename;
    private ResultSet groupsRs;
    //Rahuls changes end

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyGroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyGroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyGroupsFragment newInstance(String param1, String param2) {
        MyGroupsFragment fragment = new MyGroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    /*
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);


     */

        //Rahuls changes start
        /*
        mainActivity = (MainActivity) getActivity();
        folderID = mainActivity.currentFolderID;
        userID = mainActivity.userID;
        //groupsRs = DatabaseHandler.getGroups(userID, folderID);
        //foldersRs = DatabaseHandler.getChildFolders(folderID);

        if (!DatabaseHandler.isValidSession(mainActivity.session)) //Invalid Session, return to login
            startActivity(new Intent(getContext(), Login.class));

        super.onCreate(savedInstanceState);
        */
        mainActivity = (MainActivity) getActivity();
        userID = mainActivity.userID;
        groupsRs = DatabaseHandler.getUserOrgs(userID);
        if (!DatabaseHandler.isValidSession(mainActivity.session)) //Invalid Session, return to login
            startActivity(new Intent(getContext(), Login.class));

        super.onCreate(savedInstanceState);
        //Rahuls changes end
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //defined rootView and returned it
        View rootView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        grid = rootView.findViewById(R.id.my_groups_grid);
        populateFragment();
        return rootView;
    }

    //Rahuls changes Start
    private void populateFragment() {



        mainActivity.toolbar.setTitle(mainActivity.getHeader());

        try {
            while (groupsRs.next()) {
                int orgID = groupsRs.getInt(1);
                String orgname = groupsRs.getString(2);
                String rolename = groupsRs.getString(3);
                Group groupData = new Group(orgID, orgname, rolename);

                CardView group = new CardView(getContext());
                group.setTag(groupData);

                cardArr.add(group);
            }
            groupsRs.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




        FileAdapter adapter = new FileAdapter(getContext(), cardArr);
        grid.setAdapter(adapter);



    //Rahuls changes End
    }
}