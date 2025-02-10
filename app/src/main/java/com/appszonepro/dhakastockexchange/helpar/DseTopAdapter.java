package com.appszonepro.dhakastockexchange.helpar;

import static android.widget.Toast.LENGTH_LONG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appszonepro.dhakastockexchange.R;
import com.appszonepro.dhakastockexchange.activity.AlarmActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DseTopAdapter extends RecyclerView.Adapter<DseTopAdapter.ViewHolder> {
    private List<DseModel> dseList;

    Context context;

    public DseTopAdapter(List<DseModel> dseList) {
        this.dseList = dseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_up, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DseModel model = dseList.get(position);
        holder.company_no.setText("" + model.getId());
        holder.txtName.setText(model.getName());
        holder.txtPrice.setText("" + model.getPrice());
        holder.txtChangePrice.setText("" + model.getChangePrice());
        holder.txtPercentage.setText("" + model.getPersentage());

        holder.notification.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AlarmActivity.class);
            intent.putExtra("id", String.valueOf(model.getId()));
            intent.putExtra("name", model.getName());
            intent.putExtra("price", String.valueOf(model.getPrice()));
            intent.putExtra("changePrice", String.valueOf(model.getChangePrice()));
            intent.putExtra("percentage", String.valueOf(model.getPersentage()));
            intent.putExtra("companyNo", String.valueOf(model.getId()));

            view.getContext().startActivity(intent);
        });



        // Change arrow and card layout color based on change price
        if (model.getChangePrice() > 0.0) {
            holder.arrow.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.cardlayout.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.arrow.setImageResource(R.drawable.up_arrow);
        } else if (model.getChangePrice() < 0.0) {
            holder.arrow.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.cardlayout.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.arrow.setImageResource(R.drawable.down_arrow);
        } else {
            holder.arrow.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
            holder.cardlayout.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
            holder.arrow.setImageResource(R.drawable.level_arrow); // Change arrow image when change price is 0.0

        }
    }

    @Override
    public int getItemCount() {
        return dseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView company_no, txtName, txtPrice, txtChangePrice, txtPercentage;
        ImageView arrow,notification;
        MaterialCardView cardlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.Name);
            txtPrice = itemView.findViewById(R.id.Price);
            txtChangePrice = itemView.findViewById(R.id.Change);
            txtPercentage = itemView.findViewById(R.id.Persentage);
            company_no = itemView.findViewById(R.id.company_no);
            arrow = itemView.findViewById(R.id.arrow);
            cardlayout = itemView.findViewById(R.id.cardlayout);
            notification = itemView.findViewById(R.id.notification);
        }
    }
}
