package com.appszonepro.dhakastockexchange.RegisterAndLogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appszonepro.dhakastockexchange.MainActivity;
import com.appszonepro.dhakastockexchange.R;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btn_register;
    TextView tv_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        btn_register.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        tv_login.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
        });


    }
}