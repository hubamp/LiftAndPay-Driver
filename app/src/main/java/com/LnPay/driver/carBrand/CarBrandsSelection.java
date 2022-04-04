package com.LnPay.driver.carBrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.UploadedRidesActivity;
import com.LnPay.driver.uploadedRide.UploadedRidesAdapter;

import java.util.ArrayList;

public class CarBrandsSelection extends AppCompatActivity {

    private RecyclerView carBrandRecyclerView;
    private AutoCompleteTextView searchView;
    private carBrandAdapter carBrandAdapter;
    private ArrayList<carBrandItem> carBrandItems = new ArrayList<>();
    private carBrandItem carBrandItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_brands_selection);

        carBrandRecyclerView = findViewById(R.id.carBrandRecyclerViewId);
        searchView = findViewById(R.id.carBrandSearchViewID);



        carBrandAdapter = new carBrandAdapter(CarBrandsSelection.this, carBrandItems);
        carBrandRecyclerView.setLayoutManager(new LinearLayoutManager(CarBrandsSelection.this, LinearLayoutManager.VERTICAL, false));
        carBrandRecyclerView.setAdapter(carBrandAdapter);


    }
}