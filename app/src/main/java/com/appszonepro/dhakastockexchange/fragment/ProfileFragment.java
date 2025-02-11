package com.appszonepro.dhakastockexchange.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appszonepro.dhakastockexchange.R;

public class ProfileFragment extends Fragment {

    TextView tvGuestLogin;

    // SharedPreferences এর জন্য কন্সট্যান্ট
    private static final String USER_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_ID = "user_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvGuestLogin = view.findViewById(R.id.tvGuestLogin);

        // SharedPreferences থেকে লগইন স্টেটাস এবং ইউজার আইডি পড়া
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);
        String userId = sharedPreferences.getString(USER_ID, "Guest");

        // ইউজার লগইন অবস্থান চেক করা
        if (isLoggedIn) {
            tvGuestLogin.setText("Welcome User ID: " + userId);
        } else {
            tvGuestLogin.setText("Welcome Guest");
        }

        return view;
    }
}
