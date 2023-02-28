package com.example.myloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private Session session;
    TextView navUsername,navEmail;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    NavigationView navigationView;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DatabaseHandler.verifySession(session)) //Invalid Session
            startActivity(new Intent(getApplicationContext(), Login.class));

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = (View) navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.nav_header_username);
        navEmail = (TextView) headerView.findViewById(R.id.nav_header_email);

        int userID = session.getUserID();
        String username = DatabaseHandler.getUserColumn(userID,"username");
        String email = DatabaseHandler.getUserColumn(userID,"email");

        navUsername.setText(username);
        navEmail.setText(email);
        toolbar.setTitle("My Receipts");
        bottomNavigationView.setBackground(null);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        toggle.syncState();

        //Set "My Receipts" as default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_myreciepts);
            toolbar.setTitle("My Receipts");
        }

        //Navigation Drawer Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_myreciepts:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment(), "myreceipts").commit();
                    navigationView.setCheckedItem(R.id.nav_myreciepts);
                    toolbar.setTitle("My Receipts");
                    bottomNavigationView.getMenu().findItem(R.id.bottom_nav_myreceipts).setChecked(true);
                    break;
                case R.id.nav_mygroups:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyGroupsFragment(), "mygroups").commit();
                    navigationView.setCheckedItem(R.id.nav_mygroups);
                    toolbar.setTitle("My Groups");
                    bottomNavigationView.getMenu().findItem(R.id.bottom_nav_mygroups).setChecked(true);
                    break;
                case R.id.nav_logout:
                    navigationView.setCheckedItem(R.id.nav_logout);
                    session.clear();
                    startActivity(new Intent(getApplicationContext(), Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        //Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_nav_myreceipts) {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new MyReceiptsFragment(), "myreceipts").commit();
                navigationView.setCheckedItem(R.id.nav_myreciepts);

                toolbar.setTitle("My Receipts");
            } else {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new MyGroupsFragment(), "mygroups").commit();
                navigationView.setCheckedItem(R.id.nav_mygroups);
                toolbar.setTitle("My Groups");
            }
            return true;
        });

        //Floating Action Button Listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        MyGroupsFragment groupFrag = (MyGroupsFragment) getSupportFragmentManager().findFragmentByTag("mygroups");
        if (groupFrag!=null && groupFrag.isVisible() ) {
            dialog.setContentView(R.layout.groupbottomsheetlayout);
            LinearLayout createGroupLayout = dialog.findViewById(R.id.layoutCreateGroup);
            LinearLayout joinGroup = dialog.findViewById(R.id.layoutJoinGroup);
            createGroupLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            joinGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

        }
        else {
            dialog.setContentView(R.layout.bottomsheetlayout);
            LinearLayout folderLayout = dialog.findViewById(R.id.layoutFolder);
            LinearLayout uploadLayout = dialog.findViewById(R.id.layoutUpload);
            LinearLayout cameraLayout = dialog.findViewById(R.id.layoutCamera);
            folderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            uploadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            cameraLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}