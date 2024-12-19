package com.example.weather_forecast.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.hourlyAdapters;
import com.example.weather_forecast.domains.GeocodingAPI;
import com.example.weather_forecast.domains.NominatimSearchTask;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.WeatherResponse;
import com.example.weather_forecast.domains.hourly;
import com.example.weather_forecast.domains.weatherAPI;
import com.example.weather_forecast.domains.geoLocation;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    //search bar
    private AutoCompleteTextView searchAutoCompleteTextView;

    private TextView tvCity, tvTemperature, tvHumidity, tvDescription, tvWindSpeed, tvRainPercentage, tvFeelsLike;


    private GeocodingAPI geocodingAPI;
    private String place = "Hanoi";
    private ArrayAdapter<String> adapter;
    private List<String> suggestions = new ArrayList<>();
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Viết hoa chữ cái đầu, giữ nguyên phần còn lại
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    weatherAPI WAPI = RetrofitClient.getClient().create(weatherAPI.class);

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);

        // Adapter để hiển thị danh sách gợi ý
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );

        // Gắn adapter vào AutoCompleteTextView
        searchAutoCompleteTextView.setAdapter(adapter);

        GeocodingAPI geoAPI = RetrofitClient.getClient().create(GeocodingAPI.class);

        // Lắng nghe sự thay đổi trong AutoCompleteTextView
        searchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) { // Chỉ kiểm tra >= completionThreshold
                    fetchLocation(s.toString(), geoAPI);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        searchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lưu giá trị địa điểm đã chọn
                place = parentView.getItemAtPosition(position).toString();

                // Tắt dropdown (thực tế, AutoCompleteTextView sẽ tự động đóng dropdown khi người dùng chọn item)
                searchAutoCompleteTextView.setText("");
                searchAutoCompleteTextView.dismissDropDown();
                hideKeyboard();
                callAPI();
            }
        });
        tvCity = findViewById(R.id.city);
        tvTemperature = findViewById(R.id.temperature);
        tvFeelsLike = findViewById(R.id.feelslike);
        tvHumidity = findViewById(R.id.humidity);
        tvDescription = findViewById(R.id.description);
        tvWindSpeed = findViewById(R.id.windspeed);
        tvRainPercentage = findViewById(R.id.rainpercentage);

        callAPI();

        initRecyclerView();

        setVariable();
    }

    private void callAPI (){
        Call<WeatherResponse> call = WAPI.getWeather(place, "797561c304afd6ef5c33b5dd8dbc42e6", "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // Lấy dữ liệu từ response
                    String cityName = place;
                    float temp = weatherResponse.getMain().getTemp();
                    float feelsLike = weatherResponse.getMain().getFeelsLike();
                    int humidity = weatherResponse.getMain().getHumidity();
                    String description = weatherResponse.getWeather().get(0).getDescription();
                    description = capitalizeFirstLetter(description);
                    float wind = weatherResponse.getWind().getSpeed();
                    float rainPercentage = weatherResponse.getRainPercentage() * 100;

                    // Cập nhật giao diện
                    tvCity.setText(cityName);
                    tvTemperature.setText(Math.round(temp) + "°C");
                    tvFeelsLike.setText("Feels like " + Math.round(feelsLike) + "°C");
                    tvHumidity.setText(humidity + "%");
                    tvDescription.setText(description);
                    tvWindSpeed.setText(Math.round(wind) + " km/h");
                    tvRainPercentage.setText(Math.round(rainPercentage) + " %");
                } else {
                    tvCity.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Xử lý lỗi kết nối hoặc thời gian chờ
                tvCity.setText("Error: " + t.getMessage());
            }
        });
    }
    private void fetchLocation(String query, GeocodingAPI geoAPI) {
        geoAPI.getLocations(query, 10, "797561c304afd6ef5c33b5dd8dbc42e6").enqueue(new Callback<List<geoLocation>>() {
            @Override
            public void onResponse(Call<List<geoLocation>> call, Response<List<geoLocation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.clear(); // Xóa dữ liệu cũ
                    for (geoLocation location : response.body()) {
                        suggestions.add(location.getName());

                    }
                    adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            suggestions
                    );
                    searchAutoCompleteTextView.setAdapter(adapter);
                    searchAutoCompleteTextView.showDropDown(); // Hiển thị dropdown
                } else {
                    Toast.makeText(MainActivity.this, "No location found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<geoLocation>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setVariable() {
        TextView nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FutureActivity.class);
            startActivity(intent);  // Chuyển sang FutureActivity
        });

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

    private void initRecyclerView() {
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}


