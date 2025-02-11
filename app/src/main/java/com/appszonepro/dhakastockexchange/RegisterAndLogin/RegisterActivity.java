package com.appszonepro.dhakastockexchange.RegisterAndLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.MainActivity;
import com.appszonepro.dhakastockexchange.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btn_register;
    TextInputEditText Et_Name, Et_Mobile, Et_Password;
    TextView tv_login;

    private static final String USER_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);
        Et_Name = findViewById(R.id.Et_Name);
        Et_Mobile = findViewById(R.id.Et_Mobile);
        Et_Password = findViewById(R.id.Et_Password);

        btn_register.setOnClickListener(view -> registerUser());

        tv_login.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        // যদি ইউজার লগিন করা থাকে, সরাসরি MainActivity তে পাঠানো হবে
        if (isUserLoggedIn()) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }

    private void registerUser() {
        String name = Et_Name.getText().toString().trim();
        String mobile = Et_Mobile.getText().toString().trim();
        String password = Et_Password.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "সব তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://appszonepro.com/apps/DhakaStockExchange/RegisterLogin/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");

                if (status.equals("success")) {
                    String userId = jsonObject.getString("user_id");
                    String referralId = jsonObject.getString("referral_id");

                    // **SharedPreferences-এ ডাটা সংরক্ষণ**
                    saveUserData(userId, referralId, name, mobile);

                    Toast.makeText(this, "রেজিস্ট্রেশন সফল! আপনার রেফারেল আইডি: " + referralId, Toast.LENGTH_LONG).show();

                    // **মেইন স্ক্রিনে নিয়ে যাওয়া**
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else if (status.equals("exists")) {
                    Toast.makeText(this, "এই মোবাইল নম্বর আগে রেজিস্ট্রেশন করা হয়েছে", Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = jsonObject.getString("message");
                    Toast.makeText(this, "ত্রুটি: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "সার্ভার সংযোগ ব্যর্থ", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("mobile", mobile);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // **শেয়ারড প্রেফারেন্সে ইউজারের তথ্য সংরক্ষণ**
    private void saveUserData(String userId, String referralId, String name, String mobile) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString("user_id", userId);
        editor.putString("referral_id", referralId);
        editor.putString("user_name", name);
        editor.putString("user_mobile", mobile);

        editor.apply();
    }

    // **ইউজার লগ ইন করা আছে কিনা চেক করার ফাংশন**
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    // **শেয়ারড প্রেফারেন্স থেকে ইউজারের তথ্য রিট্রিভ করা**
    private Map<String, String> getUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);

        Map<String, String> userData = new HashMap<>();
        userData.put("user_id", sharedPreferences.getString("user_id", ""));
        userData.put("referral_id", sharedPreferences.getString("referral_id", ""));
        userData.put("user_name", sharedPreferences.getString("user_name", ""));
        userData.put("user_mobile", sharedPreferences.getString("user_mobile", ""));
        return userData;
    }

    // **লগআউট ফাংশন (যদি দরকার হয় ভবিষ্যতে)**
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // **লগআউটের পর রেজিস্ট্রেশন/লগইন স্ক্রিনে পাঠানো**
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
