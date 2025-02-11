package com.appszonepro.dhakastockexchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.RegisterAndLogin.LoginActivity;
import com.appszonepro.dhakastockexchange.fragment.DownTenFragment;
import com.appszonepro.dhakastockexchange.fragment.HomeFragment;
import com.appszonepro.dhakastockexchange.fragment.ProfileFragment;
import com.appszonepro.dhakastockexchange.fragment.TopTenFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;
    private ProgressBar progressBar;
    String token;
    String userId;
    SharedPreferences sharedPreferences;
    private static final String UserPrefs = "UserPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔹 প্রথমেই SharedPreferences ইনিশিয়ালাইজ করা হলো
        sharedPreferences = getSharedPreferences(UserPrefs, Context.MODE_PRIVATE);

        // 🔹 লগইন চেক করা হচ্ছে
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            // 🔹 লগিন না করা থাকলে LoginActivity তে রিডাইরেক্ট করা হবে
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        userId = sharedPreferences.getString("user_id", "");

        askNotificationPermission();

        bottom_navigation = findViewById(R.id.bottom_navigation);
        BottomViewChange(new HomeFragment());

        bottom_navigation.setOnNavigationItemSelectedListener(menuItem -> {
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
        });

        if (!userId.isEmpty()) {
            FirebaseRegistrationToken();
        }
    }

    private void BottomViewChange(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_logout);
        builder.setTitle("Exit Application");
        builder.setMessage("Are you sure you want to exit the application?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> finishAffinity());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // অনুমতি দেওয়া হলে কাজ করবে
                } else {
                    // অনুমতি না দিলে ইউজারকে জানানো যেতে পারে
                }
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // অনুমতি রয়েছে
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void FirebaseRegistrationToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                token = task.getResult();
                Log.d("AMARTOKEN", token);
                updateTokenToServer(token);
            }
        });
    }

    private void updateTokenToServer(String token) {

        if (!userId.isEmpty()) {
            String url = "https://appszonepro.com/apps/DhakaStockExchange/Firebase/update_token.php";
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> Log.d("TokenUpdate", "Response: " + response),
                    error -> Log.e("TokenUpdate", "Error: " + error.getMessage())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", userId);
                    params.put("token", token);
                    return params;
                }
            };
            queue.add(request);
        }
    }
}
