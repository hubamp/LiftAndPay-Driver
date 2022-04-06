package com.LnPay.driver.carBrand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.UploadedRidesActivity;
import com.LnPay.driver.uploadedRide.UploadedRidesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CarBrandsSelection extends AppCompatActivity {

    private RecyclerView carBrandRecyclerView;
    private androidx.appcompat.widget.SearchView searchView;
    private carBrandAdapter carBrandAdapter;
    private ArrayList<carBrandItem> carBrandItems = new ArrayList<>();
    private carBrandItem carBrandItem;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView closeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_brands_selection);

        carBrandRecyclerView = findViewById(R.id.carBrandRecyclerViewId);
        searchView = findViewById(R.id.carBrandSearchViewID);
        closeBtn = findViewById(R.id.closBtnId);

        closeBtn.setOnClickListener(V->{
            finish();
        });


        db.collection("CarBrands").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    carBrandItem = new carBrandItem(documentSnapshot.getId().toUpperCase(Locale.ROOT));
                    carBrandItems.add(carBrandItem);

                }

                carBrandAdapter = new carBrandAdapter(CarBrandsSelection.this, CarBrandsSelection.this, carBrandItems);
                carBrandRecyclerView.setLayoutManager(new LinearLayoutManager(CarBrandsSelection.this, LinearLayoutManager.VERTICAL, false));
                carBrandRecyclerView.setAdapter(carBrandAdapter);

                searchView.setFocusable(true);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Toast.makeText(CarBrandsSelection.this, query, Toast.LENGTH_SHORT).show();

                        List<carBrandItem> filtered = carBrandItems.stream()
                                .filter(p -> p.getCarBrand().contains(query)).collect(Collectors.toList());


                        carBrandAdapter = new carBrandAdapter(CarBrandsSelection.this, CarBrandsSelection.this, new ArrayList<>(filtered));
                        carBrandRecyclerView.setLayoutManager(new LinearLayoutManager(CarBrandsSelection.this, LinearLayoutManager.VERTICAL, false));
                        carBrandRecyclerView.setAdapter(carBrandAdapter);
                        return true;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public boolean onQueryTextChange(String newText) {

                        List<carBrandItem> filtered = carBrandItems.stream()
                                .filter(p -> p.getCarBrand().contains(newText.toUpperCase(Locale.ROOT))).collect(Collectors.toList());


                        carBrandAdapter = new carBrandAdapter(CarBrandsSelection.this, CarBrandsSelection.this, new ArrayList<>(filtered));
                        carBrandRecyclerView.setLayoutManager(new LinearLayoutManager(CarBrandsSelection.this, LinearLayoutManager.VERTICAL, false));
                        carBrandRecyclerView.setAdapter(carBrandAdapter);


                        return true;
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CarBrandsSelection.this, "Failed to get car brands", Toast.LENGTH_SHORT).show();
            }
        });


    }
}