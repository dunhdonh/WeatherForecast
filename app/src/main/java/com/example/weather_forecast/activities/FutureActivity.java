package com.example.weather_forecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.futureAdapters;
import com.example.weather_forecast.domains.future;

import java.util.ArrayList;

public class FutureActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterTomorrow;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);
        
        initRecyclerView();
        setVariable();
    }

    private void setVariable() {
        ConstraintLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> { onBackPressed();
        });
    }

    private void initRecyclerView() {
        ArrayList<future> items = new ArrayList<>();
        items.add(new future("Mon", "sunny","Sunny",30, 25));
        items.add(new future("Tue", "windy","Windy",25, 20));
        items.add(new future("Wed", "rainy","Rainy",24, 19));
        items.add(new future("Thu", "cloudy","Cloudy",26, 21));
        items.add(new future("Fri", "snowy","Snowy",18, 10));
        items.add(new future("Sat", "storm","Storm",20, 15));
        items.add(new future("Sun", "cloudy_sunny","Mostly Cloudy",28, 23));

        recyclerView = findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterTomorrow = new futureAdapters(items);

        recyclerView.setAdapter(adapterTomorrow);

    }
}
