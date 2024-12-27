package com.example.weather_forecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.futureAdapters;
import com.example.weather_forecast.domains.HourlyForecastResponse;
import com.example.weather_forecast.domains.JsonFileReader;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.future;
import com.example.weather_forecast.domains.weatherAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureActivity extends AppCompatActivity {
    Intent intent;
    String place;

    private RecyclerView.Adapter adapterTomorrow;
    public RecyclerView recyclerView;

    HourlyForecastResponse hourlyForecastResponse;

    weatherAPI WAPI = RetrofitClient.getClient().create(weatherAPI.class);
    ImageView ivTomorrow;
    TextView tvTomorrowTemp, tvTomorrowDescription, tvTomorrowRain, tvTomorrowWind, tvTomorrowHumidity;
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Viết hoa chữ cái đầu, giữ nguyên phần còn lại
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        intent = getIntent();
        place = intent.getStringExtra("place");

        ivTomorrow = findViewById(R.id.tomorrowImg);
        tvTomorrowTemp = findViewById(R.id.tomorrowTemp);
        tvTomorrowDescription = findViewById(R.id.tomorrowDescription);
        tvTomorrowRain = findViewById(R.id.tomorrowRain);
        tvTomorrowWind = findViewById(R.id.tomorrowWind);
        tvTomorrowHumidity = findViewById(R.id.tomorrowHumid);
        callAPI();
        initRecyclerView();
        setVariable();
    }

    private void updatePanels(){
        String img = hourlyForecastResponse.getWeatherData().get(6).getWeather().get(0).getIcon();
        String imgURL = "https://openweathermap.org/img/wn/" + img + "@2x.png";
        String des = hourlyForecastResponse.getWeatherData().get(6).getWeather().get(0).getDescription();
        des = capitalizeFirstLetter(des);
        float temp = hourlyForecastResponse.getWeatherData().get(6).getMain().getTemp();
        float rain = hourlyForecastResponse.getWeatherData().get(6).getRainPercentage() * 100;
        float wind = hourlyForecastResponse.getWeatherData().get(6).getWind().getSpeed();
        int humid = hourlyForecastResponse.getWeatherData().get(6).getMain().getHumidity();

        Glide.with(FutureActivity.this).load(imgURL).into(ivTomorrow);
        tvTomorrowTemp.setText(Math.round(temp) + "°");
        tvTomorrowDescription.setText(des);
        tvTomorrowRain.setText(Math.round(rain) + "%");
        tvTomorrowWind.setText(Math.round(wind) +" km/h");
        tvTomorrowHumidity.setText(humid + "%");


    }

    private void callAPI(){
        Call<HourlyForecastResponse> call = WAPI.getDailyForecast(place, "797561c304afd6ef5c33b5dd8dbc42e6", "metric");
        call.enqueue(new Callback<HourlyForecastResponse>() {
            @Override
            public void onResponse(Call<HourlyForecastResponse> call, Response<HourlyForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hourlyForecastResponse = response.body();
                    updatePanels();
                }
            }

            @Override
            public void onFailure(Call<HourlyForecastResponse> call, Throwable t) {

            }
        });
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
