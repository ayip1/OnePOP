package com.example.myloginapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.metrics.Event;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.net.HTTP.*;

public class MainActivity extends AppCompatActivity {
    private Session session;
    TextView navUsername,navEmail;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    NavigationView navigationView;
    View headerView;
    ImageView imageFrame;
    ImageView openCamera;
    String encImage;
    private static final int pic_id = 123;


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

    ExecutorService mExecutor;// = Executors.newSingleThreadExecutor();
    Handler mHandler;// = new Handler(Looper.getMainLooper());


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //System.out.println("onActivityResult was hit ------------------------------------------");
        //System.out.println("this is what is in data: " + data.getExtras().get("data"));
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == pic_id) {
                if (data != null) {
                    System.out.println("this is what is in data: " + data.getExtras().get("data"));
                    System.out.println("Picture was taken ------------------------------------------");
                    mExecutor = Executors.newSingleThreadExecutor();
                    mHandler = new Handler(Looper.getMainLooper());
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    processInBg(encImage);
                } else {
                    System.out.println("Pressed back");
                    return;
                }

            }
        }

    }

    // Create some member variables for the ExecutorService
// and for the Handler that will update the UI from the main thread


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
                        System.out.println(result);

                        //   category, store name, total, barcode number, date, payment method
                        //  .category
                        //  .date
                        //  .payment.card_number (card number if there is one)
                        //  .payment.display_name (type of pmt)
                        //  .subtotal (total w/o tax)
                        //  .tax (tax, add to subtotal to get total)
                        //


                        // If we're done with the ExecutorService, shut it down.
                        // (If you want to re-use the ExecutorService,
                        // make sure to shut it down whenever everything's completed
                        // and you don't need it any more.)

                            mExecutor.shutdown();

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
        post.addHeader("CLIENT-ID", "vrfkShpCnrpmGGnFKEFiP4g8A58fLjLukGRjpIM");
        post.addHeader("AUTHORIZATION", "apikey veryfi11:050fb329fdbfcaaeb720671fa0ef582c");
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
                @SuppressLint("WrongViewCast")
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    openCamera = findViewById(R.id.cameraButton);

                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, pic_id);



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