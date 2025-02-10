package com.appszonepro.dhakastockexchange.fragment;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.R;
import com.appszonepro.dhakastockexchange.helpar.DseAdapter;
import com.appszonepro.dhakastockexchange.helpar.DseModel;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private RecyclerView recyclerView;
    private DseAdapter adapter;
    private List<DseModel> dseList;
    private LottieAnimationView lottieAnimation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String URL = "https://appszonepro.com/apps/DhakaStockExchange/fetch_data.php"; // তোমার সার্ভার লিংক

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // UI Initialization
        toolbar = view.findViewById(R.id.toolbar);
        navView = view.findViewById(R.id.nav_view);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        lottieAnimation = view.findViewById(R.id.lottieAnimation);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dseList = new ArrayList<>();
        adapter = new DseAdapter(dseList);
        recyclerView.setAdapter(adapter);

        // Drawer Setup
        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.closs);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Click Listener
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.topten) {
                Toast.makeText(getContext(), "Top ten", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.downten) {
                Toast.makeText(getContext(), "Down ten", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.profile) {
                Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return false;
        });

        // Swipe to Refresh Listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchData(); // ডাটা লোড করবে
        });

        fetchData();

        return view;
    }

    private void fetchData() {

        lottieAnimation.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    lottieAnimation.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false); // সোয়াইপ রিফ্রেশ বন্ধ করবো
                    dseList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            DseModel model = new DseModel(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    (float) obj.getDouble("price"),
                                    (float) obj.getDouble("change_price"),
                                    obj.getString("persentage"),
                                    obj.getString("time")
                            );
                            dseList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    lottieAnimation.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false); // সোয়াইপ রিফ্রেশ বন্ধ করবো
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", "Error: " + error.getMessage());
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
