package com.appszonepro.dhakastockexchange.RegisterAndLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appszonepro.dhakastockexchange.MainActivity;
import com.appszonepro.dhakastockexchange.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

    MaterialButton btn_login;
    TextInputEditText et_mobile, et_password;
    private static final String LOGIN_URL = "https://appszonepro.com/apps/DhakaStockExchange/RegisterLogin/login.php"; // 🔹 আপনার সার্ভারের URL দিন

    private static final String USER_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        et_mobile = findViewById(R.id.et_mobile);
        et_password = findViewById(R.id.et_password);

        // লগইন চেক করা হচ্ছে
        if (isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btn_login.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String mobile = et_mobile.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "সব ঘর পূরণ করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            String userId = jsonObject.getString("user_id");

                            // **SharedPreferences-এ ইউজারের তথ্য সংরক্ষণ**
                            SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(IS_LOGGED_IN, true); // লগইন স্টেটাস সংরক্ষণ
                            editor.putString("user_id", userId);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "লগিন সফল!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LoginActivity.this, "সংযোগ সমস্যা! পরে চেষ্টা করুন।", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    // **লগইন চেক করার ফাংশন**
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false); // যদি ইউজার লগইন করা থাকে
    }
}
