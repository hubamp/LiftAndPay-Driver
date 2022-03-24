package com.LnPay.liftandpay_api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LnPayAPI_Call extends Observable {

    private driverModel driver;
    private boolean received;
    public ArrayList<driver> drivers;


    public LnPayAPI_Call() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LnPayAPI_Interface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LnPayAPI_Interface driverInterface = retrofit.create(LnPayAPI_Interface.class);

        driverInterface.getDrivers().enqueue(new Callback<driverModel>()
        {
            @Override
            public void onResponse(Call<driverModel> call, Response<driverModel> response)
            {
                driver di = new driver();
                di.setId("1");


                drivers = response.body().getDrivers();


                Log.i("driverInterfaceLog","Received Here");
                Log.i("driverInterfaceLog",response.body().getDrivers().indexOf(di)+"");

            }

            @Override
            public void onFailure(Call<driverModel> call, Throwable t)
            {
                Log.i("driverInterfaceLog",t.getMessage());

            }
        });
    }

    public ArrayList<driver>  getDriver() {

        if(drivers == null){
            return getDriver();
        }
        else{
            return drivers;
        }

    }

    private void setReceived(String received) {
        setChanged();
        notifyObservers(received);
    }

    private boolean isReceived() {
        return received;
    }


    public void setDriver(driverModel driver) {
        this.driver = driver;
    }

    @Override
    public synchronized boolean hasChanged() {
        return super.hasChanged();
    }
}


