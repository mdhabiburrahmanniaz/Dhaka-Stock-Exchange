package com.appszonepro.dhakastockexchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.fragment.DownTenFragment;
import com.appszonepro.dhakastockexchange.fragment.HomeFragment;
import com.appszonepro.dhakastockexchange.fragment.ProfileFragment;
import com.appszonepro.dhakastockexchange.fragment.TopTenFragment;
import com.appszonepro.dhakastockexchange.helpar.DseAdapter;
import com.appszonepro.dhakastockexchange.helpar.DseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView bottom_navigation;

    private RecyclerView recyclerView;
    private DseAdapter adapter;
    private List<DseModel> dseList;
    private ProgressBar progressBar;

    String token;
    private static final String URL = "https://appszonepro.com/apps/DhakaStockExchange/fetch_data.php"; // তোমার সার্ভার লিংক

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askNotificationPermission();
        FirebaseRegistationToken();
        sendTokenToServer(token);


        bottom_navigation = findViewById(R.id.bottom_navigation);

        BottomViewChange(new HomeFragment());

        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.home) {
                    BottomViewChange(new HomeFragment());
                } else if (menuItem.getItemId() == R.id.topten) {
                    BottomViewChange(new TopTenFragment());
                } else if (menuItem.getItemId() == R.id.downten) {
                    BottomViewChange(new DownTenFragment());
                } else if (menuItem.getItemId() == R.id.profile) {
                    BottomViewChange(new ProfileFragment());

                }

                return true;
            }
        });


    } //=========================


    private void BottomViewChange(Fragment fragment) {

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitDialog();

    }

    private void showExitDialog() {
        // AlertDialog তৈরি
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_logout);
        builder.setTitle("Exit Application");
        builder.setMessage("Are you sure you want to exit the application?");
        builder.setCancelable(false); // Back button প্রেস করলে ডায়লগ ডিসমিস হবে না

        // "Yes" বোতামের কাজ
        builder.setPositiveButton("Yes", (dialog, which) -> {
            finishAffinity(); // অ্যাপ থেকে বের হয়ে যাবে
        });

        // "No" বোতামের কাজ
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss(); // ডায়লগটি বন্ধ হবে
        });

        // ডায়লগটি দেখানো
        AlertDialog dialog = builder.create();
        dialog.show();
    } //==========


    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_notifications)
                        .setTitle("Notification")
                        .setMessage("Please Permission")
                        .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // code here
                            }
                        })
                        .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // code here
                            }
                        })
                        .create()
                        .show();


            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    } //----------------


    private void FirebaseRegistationToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASETOKEN", "Fetching FCM registration token failed", task.getException());
                            return;

                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        Log.d("FIREBASETOKEN", token);
                    }
                });


    } //=========


    private void sendTokenToServer(String token) {
        // সার্ভারের URL
        String url = "http://appszonepro.com/apps/DhakaStockExchange/Firebase/update_token.php";

        // Volley রিকোয়েস্ট তৈরি
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // সার্ভার থেকে রেসপন্স
                    System.out.println("টোকেন সফলভাবে আপডেট হয়েছে: " + response);
                },
                error -> {
                    // এরর হ্যান্ডলিং
                    System.out.println("টোকেন আপডেটে সমস্যা: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // প্যারামিটারস হিসেবে টোকেন পাঠানো
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("user_id", "1"); // ইউজার আইডি বা অন্য কোনো আইডেন্টিফায়ার
                return params;
            }
        };

        // রিকোয়েস্ট কিউতে রিকোয়েস্ট যোগ করুন
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}