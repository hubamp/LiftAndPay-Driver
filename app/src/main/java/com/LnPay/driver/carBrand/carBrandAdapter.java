package com.LnPay.driver.carBrand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.R;

import java.util.ArrayList;

public class carBrandAdapter extends RecyclerView.Adapter<carBrandAdapter.carBrandHolder> {


    Activity activity;
    Context context;
    ArrayList<carBrandItem> carBrandItems;

    public carBrandAdapter(Activity activity, Context context, ArrayList<carBrandItem> carBrandItems) {
        this.activity = activity;
        this.context = context;
        this.carBrandItems = carBrandItems;
    }

    @NonNull
    @Override
    public carBrandAdapter.carBrandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_carbrand, parent, false);
        return new carBrandAdapter.carBrandHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull carBrandAdapter.carBrandHolder holder, int position) {

        holder.carBrandText.setText(carBrandItems.get(holder.getAdapterPosition()).getCarBrand());

        holder.carBrandText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedBrand = holder.carBrandText.getText().toString();

                Intent i = new Intent();
                i.putExtra("theSelectedBrand", selectedBrand);
                activity.setResult(Activity.RESULT_OK, i);
                activity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return carBrandItems.size();
    }

    public class carBrandHolder extends RecyclerView.ViewHolder {

        TextView carBrandText;

        public carBrandHolder(@NonNull View itemView) {
            super(itemView);

            carBrandText = itemView.findViewById(R.id.carBrandTxtId);
        }
    }
}
