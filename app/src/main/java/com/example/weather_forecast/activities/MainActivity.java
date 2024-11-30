package com.example.weather_forecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.hourlyAdapters;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.WeatherResponse;
import com.example.weather_forecast.domains.hourly;
import com.example.weather_forecast.domains.weatherAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);
        initRecyclerView();

        setVariable();
    }

    private void setVariable() {
        TextView nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v->startActivity(new Intent(MainActivity.this, FutureActivity.class)));
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void initRecyclerView(){
        ArrayList<hourly> items = new ArrayList<>();

        items.add(new hourly("9 pm", 28, "cloudy"));
        items.add(new hourly("11 pm", 29, "sunny"));
        items.add(new hourly("12 pm", 24, "wind"));
        items.add(new hourly("1 am", 25, "rainy"));
        items.add(new hourly("3 am", 26, "storm"));

        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterHourly = new hourlyAdapters(items);
        recyclerView.setAdapter(adapterHourly);

    }
}