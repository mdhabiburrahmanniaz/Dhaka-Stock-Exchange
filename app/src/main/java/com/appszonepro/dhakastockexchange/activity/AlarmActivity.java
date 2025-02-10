package com.appszonepro.dhakastockexchange.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity {
    MaterialCardView submitButton;
    TextInputEditText EdAlarm;
    TextView Company_Name;
    String userId = "1"; // এখানে লগইন ইউজারের ID সেট করুন

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        submitButton = findViewById(R.id.submitButton);
        Company_Name = findViewById(R.id.Company_Name);
        EdAlarm = findViewById(R.id.EdAlarm);

        // Intent থেকে ডাটা রিসিভ করুন
        String Stock_id = getIntent().getStringExtra("id");
        String Name = getIntent().getStringExtra("name");
        String Price = getIntent().getStringExtra("price");
        String changePrice = getIntent().getStringExtra("changePrice");


        Company_Name.setText("ID: "+Stock_id+"\nCompany Name: "+Name + "\nPrice: " + Price+"\nChange Price: "+changePrice);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alertPrice = EdAlarm.getText().toString().trim();

                if (alertPrice.isEmpty()) {
                    Toast.makeText(AlarmActivity.this, "দয়া করে অ্যালার্ম প্রাইস লিখুন", Toast.LENGTH_SHORT).show();
                } else {
                    sendAlarmData(Stock_id,userId, Name, alertPrice);
                }
            }
        });
    }

    private void sendAlarmData(String Stock_id ,String userId, String name, String alertPrice) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("সাবমিট করা হচ্ছে...");
        progressDialog.show();

        String url = "https://appszonepro.com/apps/DhakaStockExchange/Firebase/insert_alarm.php"; // এখানে আপনার সার্ভারের URL দিন

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(AlarmActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AlarmActivity.this, "ত্রুটি হয়েছে!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Log.d("ERROR",error.toString());
                        Toast.makeText(AlarmActivity.this, "নেটওয়ার্ক সমস্যা! আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("stock_id", Stock_id);
                params.put("user_id", userId);
                params.put("name", name);
                params.put("change_price", alertPrice);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
