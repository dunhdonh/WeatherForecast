package com.example.weather_forecast.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.hourlyAdapters;
import com.example.weather_forecast.domains.DateTimeUtil;
import com.example.weather_forecast.domains.GeocodingAPI;
import com.example.weather_forecast.domains.HourlyForecastResponse;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.WeatherResponse;
import com.example.weather_forecast.domains.hourly;
import com.example.weather_forecast.domains.weatherAPI;
import com.example.weather_forecast.domains.geoLocation;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    //search bar
    private AutoCompleteTextView searchAutoCompleteTextView;

    private TextView tvCity, tvTemperature, tvHumidity, tvDescription, tvWindSpeed, tvRainPercentage, tvFeelsLike;

    private ImageView ivMainIcon, searchIcon;

    ConstraintLayout layout;
    Drawable drawable;

    float temp;

    HourlyForecastResponse hourlyForecastResponse;
    //private WeatherViewModel vModel;
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

        searchIcon = findViewById(R.id.search_icon);
        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);

        layout = findViewById(R.id.MainLayout);
        drawable = layout.getBackground();

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchAutoCompleteTextView.getVisibility() == View.GONE) {
                    searchAutoCompleteTextView.setVisibility(View.VISIBLE); // Hiện AutoCompleteTextView
                }
            }
        });
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
                String currentText = searchAutoCompleteTextView.getText().toString(); // Lấy trực tiếp nội dung

                if (currentText.length() >= 2) {
                    fetchLocation(currentText, geoAPI); // Gọi API khi có ít nhất 2 ký tự
                    searchAutoCompleteTextView.showDropDown();

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
                searchAutoCompleteTextView.setText("");
                // Tắt dropdown (thực tế, AutoCompleteTextView sẽ tự động đóng dropdown khi người dùng chọn item)
                searchAutoCompleteTextView.dismissDropDown();
                hideKeyboard();
                searchAutoCompleteTextView.setVisibility(View.GONE); // Ẩn AutoCompleteTextView
                callAPI();
                initRecyclerView();

            }
        });
        tvCity = findViewById(R.id.city);
        tvTemperature = findViewById(R.id.temperature);
        tvFeelsLike = findViewById(R.id.feelslike);
        tvHumidity = findViewById(R.id.humidity);
        tvDescription = findViewById(R.id.description);
        tvWindSpeed = findViewById(R.id.windspeed);
        tvRainPercentage = findViewById(R.id.rainpercentage);
        ivMainIcon = findViewById(R.id.weatherIcon);

        callAPI();

        initRecyclerView();

        setVariable();
    }

    private void updateBackGround(float temp){

        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            String colorStart, colorEnd;
            if (temp < 5) {colorStart = "#15F5FD"; colorEnd = "#036CDA";}
            else if (temp < 15) {colorStart = "#036CDA"; colorEnd = "#429421";}
            else if (temp < 20) {colorStart = "#429421"; colorEnd = "#B3EB50";}
            else if (temp < 28) {colorStart = "#B3EB50"; colorEnd = "#3425AF";}
            else if (temp < 30) {colorStart = "#FF57B9"; colorEnd = "#A704FD";}
            else  {colorStart = "#F36265"; colorEnd = "#961276";}

            gradientDrawable.setColors(new int[] {Color.parseColor(colorStart), Color.parseColor(colorEnd)}); 

            // Nếu cần thay đổi hướng của gradient hoặc các thuộc tính khác
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TL_BR); // Hướng gradient mới
        }
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
                    temp = weatherResponse.getMain().getTemp();

                    String icon = weatherResponse.getWeather().get(0).getIcon();
                    String iconURL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

                    float feelsLike = weatherResponse.getMain().getFeelsLike();
                    int humidity = weatherResponse.getMain().getHumidity();
                    String description = weatherResponse.getWeather().get(0).getDescription();
                    description = capitalizeFirstLetter(description);
                    float wind = weatherResponse.getWind().getSpeed();
                    float rainPercentage = weatherResponse.getRainPercentage() * 100;

                    // Cập nhật giao diện
                    updateBackGround(temp);
                    Glide.with(MainActivity.this)
                            .load(iconURL)
                            .into(ivMainIcon);

                    tvCity.setText(cityName);
                    tvTemperature.setText(Math.round(temp) + "°");
                    tvFeelsLike.setText("Feels like " + Math.round(feelsLike) + "°");
                    tvHumidity.setText(humidity + "%");
                    tvDescription.setText(description);
                    tvWindSpeed.setText(Math.round(wind) + " km/h");
                    tvRainPercentage.setText(Math.round(rainPercentage) + "%");
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
        geoAPI.getLocations(query, 5, "797561c304afd6ef5c33b5dd8dbc42e6").enqueue(new Callback<List<geoLocation>>() {
            @Override
            public void onResponse(Call<List<geoLocation>> call, Response<List<geoLocation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.clear(); // Xóa dữ liệu cũ
                    for (geoLocation location : response.body()) {
                        suggestions.add(location.getName());

                    }
                    adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            R.layout.dropdown_item1,
                            suggestions
                    );
                    
                    searchAutoCompleteTextView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //searchAutoCompleteTextView.showDropDown(); // Hiển thị dropdown
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
            intent.putExtra("place", place);
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

        Call<HourlyForecastResponse> call = WAPI.getDailyForecast(place, "797561c304afd6ef5c33b5dd8dbc42e6", "metric");
        call.enqueue(new Callback<HourlyForecastResponse>() {
            @Override
            public void onResponse(Call<HourlyForecastResponse> call, Response<HourlyForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hourlyForecastResponse = response.body();
                    //vModel.updateWeatherData(hourlyForecastResponse);
                    for (int i = 0; i <= 10; i++) {
                        // Lấy dữ liệu từ response
                        long dt = hourlyForecastResponse.getWeatherData().get(i).getDt();
                        LocalDateTime dateTime = DateTimeUtil.getLocalDateTimeFromTimestamp(dt);
                        String datetime = dateTime.toString();
                        String hour = DateTimeUtil.getTimeFromTimestamp(dt);
                        float temp = hourlyForecastResponse.getWeatherData().get(i).getMain().getTemp();
                        String icon = hourlyForecastResponse.getWeatherData().get(i).getWeather().get(0).getIcon();
                        String iconURL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

                        // Cập nhật giao diện


                        items.add(new hourly(hour, Math.round(temp), iconURL));
                    }
                    recyclerView = findViewById(R.id.hourlyWeather);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

                    adapterHourly = new hourlyAdapters(items);
                    recyclerView.setAdapter(adapterHourly);

                }
            }

            @Override
            public void onFailure(Call<HourlyForecastResponse> call, Throwable t) {
                // Xử lý lỗi kết nối hoặc thời gian chờ
                tvCity.setText("Error: " + t.getMessage());
            }
        });




    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}


