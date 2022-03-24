package com.LnPay.driver.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.LnPay.driver.R;
import com.LnPay.driver.overpass.model;
import com.LnPay.driver.overpass.modelface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<searchItemModel> searchItemModels = new ArrayList<>();
    public final String theNameID = "theLocationName";
    public final String theAddressID = "theLocationAddress";
    public final  String theLatID =  "theLat";
    public final  String theLonID =  "theLon";

    private TextView infoText;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchbarId);
        recyclerView = findViewById(R.id.recyclerViewId);
        infoText = findViewById(R.id.infoText);
        progressBar = findViewById(R.id.progress);
        SearchAdapter searchAdapter;


        searchView.setIconified(false);
        searchView.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query.trim()+" ";
                if (query.trim().length() >= 2) {

                    infoText.setVisibility(View.VISIBLE);
                    infoText.setText("Searching ...");
                    progressBar.setVisibility(View.VISIBLE);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://nominatim.openstreetmap.org/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    modelface modelface = retrofit.create(modelface.class);

                    Call<ArrayList<model>> call = modelface.getData(query, "gh", 7, "json");

                    searchItemModels.clear();

                    call.enqueue(new Callback<ArrayList<model>>() {
                        @Override
                        public void onResponse(Call<ArrayList<model>> call, Response<ArrayList<model>> response) {
                            if (response.code() == 200) {

                                Log.e("response","Successful");


                                assert response.body() != null;
                                if (response.body().size()>0) {
                                    Log.e("response", "Not empty");

                                    for (int i =0; i<response.body().size();i++)
                                    {
                                        String name = response.body().get(i).getDisplayName();

                                        String[] arrOfStr = name.split(", ");

                                        String theName = "", theAddress = "";


                                        int iterate =0;
                                        for (String theString: arrOfStr)
                                        {

                                            if (iterate<2)
                                            {
                                                theName = theName.concat(theString+", ");
                                            }
                                            else if(iterate<6)
                                            {
                                                theAddress = theAddress.concat(theString+", ");
                                            }


                                            iterate++;
                                        }

                                        theAddress = theAddress.concat(response.body().get(i).getType());


                                        double theLat = response.body().get(i).getLat();
                                        double theLon = response.body().get(i).getLon();

                                        searchItemModel searchItemModel = new searchItemModel(theName, theAddress, theLon, theLat);
                                        searchItemModels.add(searchItemModel);

                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(new SearchAdapter(SearchActivity.this,SearchActivity.this,searchItemModels));

                                    infoText.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                }

                            } else {
                                Log.e("response","No response");
                                infoText.setText("No result");
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<model>> call, Throwable t) {
                            Log.e("Retrofit", t.getMessage());
                            infoText.setText("Slow internet connection");
                            progressBar.setVisibility(View.GONE);
                        }

                    });
                }
                
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


}