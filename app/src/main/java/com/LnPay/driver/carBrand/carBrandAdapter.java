package com.LnPay.driver.carBrand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.UploadedRidesAdapter;
import com.LnPay.driver.uploadedRide.uploadedRidesModel;

import java.util.ArrayList;

public class carBrandAdapter extends RecyclerView.Adapter<carBrandAdapter.carBrandHolder> {


    Context context;
    ArrayList<carBrandItem> carBrandItems;

    public carBrandAdapter(Context context, ArrayList<carBrandItem> carBrandItems) {
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

    }

    @Override
    public int getItemCount() {
        return carBrandItems.size();
    }

    public class carBrandHolder extends RecyclerView.ViewHolder {

        TextView carBandText;

        public carBrandHolder(@NonNull View itemView) {
            super(itemView);

          carBandText = itemView.findViewById(R.id.carBrandTxtId);
        }
    }
}
