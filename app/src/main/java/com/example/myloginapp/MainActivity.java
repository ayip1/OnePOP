package com.example.myloginapp;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import android.app.Activity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myloginapp.Database.DatabaseHandler;
import com.example.myloginapp.Data.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import android.util.Base64;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    public Session session;
    public int userID;
    public int currentOrgID;
    public int rootFolderID;
    public int parentFolderID;
    public int currentFolderID;
    TextView navUsername,navEmail;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    public Toolbar toolbar;
    NavigationView navigationView;
    View headerView;
    ImageView imageFrame;
    ImageView openCamera;
    private ProgressDialog loadingScreen;

    private static final int pic_id = 123;
    public static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new Session(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DatabaseHandler.isValidSession(session)) //Invalid Session, return to login
            startActivity(new Intent(getApplicationContext(), Login.class));

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = (View) navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.nav_header_username);
        navEmail = (TextView) headerView.findViewById(R.id.nav_header_email);


        userID = session.getUserID();
        rootFolderID = DatabaseHandler.getUserRootFolder(userID);
        currentFolderID = rootFolderID;
        parentFolderID = -1;
        currentOrgID = -1;

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
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment(), "myreceipts").commit();
                    navigationView.setCheckedItem(R.id.nav_myreciepts);
                    toolbar.setTitle("My Receipts");
                    bottomNavigationView.getMenu().findItem(R.id.bottom_nav_myreceipts).setChecked(true);
                    currentFolderID = rootFolderID;
                    break;
                case R.id.nav_mygroups:
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
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
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new MyReceiptsFragment(), "myreceipts").commit();
                navigationView.setCheckedItem(R.id.nav_myreciepts);
                currentFolderID = rootFolderID;
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

        // Loading Screen
        loadingScreen = new ProgressDialog(this);
        loadingScreen.setCancelable(false);
        loadingScreen.setMessage("Loading...");


    }


    ExecutorService mExecutor;// = Executors.newSingleThreadExecutor();
    Handler mHandler;// = new Handler(Looper.getMainLooper());

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        if (resultCode == RESULT_OK) {
            // Refresh the activity as needed
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_myreciepts);
            toolbar.setTitle(getHeader());
            Context context = getApplicationContext();
            String text = "worked";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } */

        if (resultCode == 123) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_myreciepts);
            toolbar.setTitle(getHeader());
        }
        else if (resultCode != RESULT_CANCELED ) {
            if (requestCode == pic_id) {
                if (data != null) {

                    mExecutor = Executors.newSingleThreadExecutor();
                    mHandler = new Handler(Looper.getMainLooper());

                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String base64Image = Base64.encodeToString(b, Base64.DEFAULT);

                    processInBg(base64Image);
                    loadingScreen.show();

                } else {
                    System.out.println("Pressed back");
                    return;
                }
            }
            if (requestCode == PICK_IMAGE) {
                Uri fileURI = data.getData();
                String base64Image = convertImageToBase64(fileURI);

                mExecutor = Executors.newSingleThreadExecutor();
                mHandler = new Handler(Looper.getMainLooper());

                processInBg(base64Image);
                loadingScreen.show();

            }
        }

    }


    //Creates new ReceiptConfirmation Activity and forwards JSON data
    public void updateUI(String receiptData) throws JSONException {
        JSONObject receiptJSON = new JSONObject(receiptData);
        Intent intent = new Intent(this, ReceiptConfirmation.class);
        intent.putExtra("JSONString", receiptJSON.toString());
        intent.putExtra("folderID", currentFolderID);
        startActivityForResult(intent, REQUEST_CODE);

    }


    public String convertImageToBase64(Uri uri) {
        try {
            //ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Create an interface to respond with the result after processing
    public interface OnProcessedListener {
        public void onProcessed(String result);
    }

    private void processInBg(final String base64Image){
        final OnProcessedListener listener = new OnProcessedListener(){
            @Override
            public void onProcessed(String result){
                // Use the handler so we're not trying to update the UI from the bg thread
                mHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        // Update the UI here
                        //updateUi(result);

                        loadingScreen.dismiss();
                        mExecutor.shutdown();
                        System.out.println(result);
                        try {
                            updateUI(result);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        };

        Runnable backgroundRunnable = new Runnable(){
            @Override
            public void run(){
                // Perform your background operation(s) and set the result(s)
                String result = null;
                try {
                    result = processReceipt(base64Image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //add error handling for network issues

                // Use the interface to pass along the result
                listener.onProcessed(result);
            }
        };

        mExecutor.execute(backgroundRunnable);
    }


    public String processReceipt(String base64Image) throws IOException, JSONException {
        System.out.println("processReceipt() function called");
        String fileName = "test";
        String fileData = base64Image;
        String result = "";

        HttpPost post = new HttpPost( "https://api.veryfi.com/api/v8/partner/documents" );
        post.addHeader( "Content-Type", "application/json" );
        post.addHeader( "Accept", "application/json" );
        //post.addHeader("CLIENT-ID", "vrfkShpCnrpmGGnFKEFiP4g8A58fLjLukGRjpIM");
        //post.addHeader("AUTHORIZATION", "apikey veryfi11:050fb329fdbfcaaeb720671fa0ef582c");
        post.addHeader("CLIENT-ID", "vrfO4gFBl1b3T6PrwSj3ax9psgPHLHrsiMXEbnY");
        post.addHeader("AUTHORIZATION", "apikey jaeyul.dl:caf84bc5c54d7bd39058385fc83c0532");
        JSONObject requestBody = new JSONObject();
        requestBody.put("file_name", fileName);
        requestBody.put("file_data", fileData);
        String requestBodyStr = requestBody.toString();
        StringEntity entity = new StringEntity(requestBodyStr);

        post.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

            result = EntityUtils.toString(response.getEntity());
        }

        return result;

    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
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
                    // Create the AlertDialog and set its title and message
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Enter folder name");

                    // Create the EditText view and add it to the AlertDialog
                    final EditText input = new EditText(MainActivity.this);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(200,  ViewGroup.LayoutParams.WRAP_CONTENT);

                    builder.setView(input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String folderName = input.getText().toString();
                            int ownerID = (currentOrgID==-1) ? userID : currentOrgID;
                            boolean isOrg = (currentOrgID==-1) ? true : false;
                            DatabaseHandler.insertFolder(ownerID, currentFolderID, folderName, isOrg);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyReceiptsFragment()).commit();
                            navigationView.setCheckedItem(R.id.nav_myreciepts);
                            toolbar.setTitle(getHeader());

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Show the AlertDialog
                    builder.show();
                }
            });

            uploadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                    dialog.dismiss();


                }
            });

            cameraLayout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongViewCast")
                @Override
                public void onClick(View v) {



                    openCamera = findViewById(R.id.cameraButton);

                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, pic_id);

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

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        if (currentFolderID!=rootFolderID) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            currentFolderID = parentFolderID;
            parentFolderID = DatabaseHandler.getParentFolderID(currentFolderID);
            replaceFragment(new MyReceiptsFragment());
        }

    }

    public String getHeader() {
        String header = "";
        if (currentFolderID==rootFolderID) {
            header = "My Receipts";
        }
        else if (currentFolderID!=-1) {
            header =  DatabaseHandler.getFolderName(currentFolderID);
        }
        //else org
        return header;
    }


}