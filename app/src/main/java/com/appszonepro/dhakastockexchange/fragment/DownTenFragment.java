package com.appszonepro.dhakastockexchange.fragment;

import android.os.Bundle;

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
import com.appszonepro.dhakastockexchange.helpar.DseDownAdapter;
import com.appszonepro.dhakastockexchange.helpar.DseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DownTenFragment extends Fragment {

    private RecyclerView recyclerView;
    private DseDownAdapter adapter;
    private List<DseModel> dseList;
    LottieAnimationView lottieAnimation;
    private static final String URL = "https://appszonepro.com/apps/DhakaStockExchange/downten.php"; // তোমার সার্ভার লিংক

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_down_ten, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        lottieAnimation = view.findViewById(R.id.lottieAnimation);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dseList = new ArrayList<>();
        adapter = new DseDownAdapter(dseList);
        recyclerView.setAdapter(adapter);



        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchData();
        });
        fetchData();


        return view;
    } //===========

    private void fetchData() {
        lottieAnimation.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lottieAnimation.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Volley", "Error: " + error.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }//=====================
}