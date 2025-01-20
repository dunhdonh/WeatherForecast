package com.example.weather_forecast.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.hourlyAdapters;
import com.example.weather_forecast.adapters.placeAdapters;
import com.example.weather_forecast.domains.DatabaseHelper;
import com.example.weather_forecast.domains.DateTimeUtil;
import com.example.weather_forecast.domains.GeocodingAPI;
import com.example.weather_forecast.domains.HourlyForecastResponse;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.WeatherResponse;
import com.example.weather_forecast.domains.hourly;
import com.example.weather_forecast.domains.savedPlace;
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

    private placeAdapters adapterPlace;
    private RecyclerView recyclerView;
    private RelativeLayout recentlyMain;
    //search bar
    private AutoCompleteTextView searchAutoCompleteTextView;

    private ImageView menu_bar;
    private RecyclerView recentCity;
    private View overlay;
    private TextView tvCity, tvTemperature, tvHumidity, tvDescription, tvWindSpeed, tvRainPercentage, tvFeelsLike;
    private TextView tvDate, tvTime;
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

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
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

        menu_bar = findViewById(R.id.menu_icon);
        recentCity = findViewById(R.id.recentCity);
        recentlyMain = findViewById(R.id.recentCityMain);
        overlay = findViewById(R.id.overlay);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        layout = findViewById(R.id.MainLayout);
        drawable = layout.getBackground();

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAutoCompleteTextView.setVisibility(View.GONE); // Hiện AutoCompleteTextView
                recentlyMain.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchAutoCompleteTextView.setVisibility(View.VISIBLE);
                recentlyMain.setVisibility(View.GONE);
                overlay.setVisibility(View.VISIBLE);
                searchAutoCompleteTextView.bringToFront();


            }
        });

        menu_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentlyMain.setVisibility(View.VISIBLE);
                searchAutoCompleteTextView.setVisibility(View.GONE);
                overlay.setVisibility(View.VISIBLE);
                recentlyMain.bringToFront();
                initRecentPlace();

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
                overlay.setVisibility(View.GONE);
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
        tvDate = findViewById(R.id.Date);
        tvTime = findViewById(R.id.Time);

        callAPI();

        initRecyclerView();

        setVariable();
    }

    private void updateBackGround(float temp){

        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            String colorStart, colorEnd;
            if (temp < 0) {colorStart = "#1A237E"; colorEnd = "#0D47A1";}
            else if (temp < 10) {colorStart = "#0D47A1"; colorEnd = "#1565C0";}
            else if (temp < 20) {colorStart = "#2565C0"; colorEnd = "#388E3C";}
            else if (temp < 30) {colorStart = "#388E3C"; colorEnd = "#7F9800";}
            else {colorStart = "#BF9800"; colorEnd = "#D71C1C";}

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

                    long dt = weatherResponse.getDt();
                    LocalDateTime dateTime = DateTimeUtil.getLocalDateTimeFromTimestamp(dt);
                    String hour = DateTimeUtil.getTimeFromTimestamp(dt);
                    String date = DateTimeUtil.getDateFromTimestamp(dt);

                    String icon = weatherResponse.getWeather().get(0).getIcon();
                    String iconURL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

                    float feelsLike = weatherResponse.getMain().getFeelsLike();
                    int humidity = weatherResponse.getMain().getHumidity();
                    String description = weatherResponse.getWeather().get(0).getDescription();
                    description = capitalizeFirstLetter(description);
                    float wind = weatherResponse.getWind().getSpeed();
                    float rainPercentage = weatherResponse.getRainPercentage() * 100;

                    addOrUpdateCity(place, temp, iconURL);

                    // update ui
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
                    tvDate.setText(date);
                    tvTime.setText(hour);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load data, try again!", Toast.LENGTH_SHORT).show();
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

    private void initRecentPlace() {
        ArrayList<savedPlace> items = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM location", null);

        for (String columnName : cursor.getColumnNames()) {
            Log.d("ColumnName", columnName);
        }

        if (cursor.moveToFirst()) {
            do {
                String place = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                String icon = cursor.getString(cursor.getColumnIndexOrThrow("icon"));
                float temp = cursor.getFloat(cursor.getColumnIndexOrThrow("temperature"));
                items.add(new savedPlace(place, Math.round(temp), icon));
            } while (cursor.moveToNext());
        }
        cursor.close();

        recyclerView = findViewById(R.id.recentCity);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

        adapterPlace = new placeAdapters(items);
        recyclerView.setAdapter(adapterPlace);

        adapterPlace.setOnItemClickListener(position -> {
            // Lấy item được click
            savedPlace selectedPlace = items.get(position);

            place = selectedPlace.getPlace();
            callAPI();
            recentlyMain.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        });

    }




    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void addOrUpdateCity(String city, float temp, String iconURL){
        Cursor cs = db.query("location", new String[]{"id", "city", "temperature", "icon"},
                "city=?", new String[]{city}, null, null, null);

        ContentValues value = new ContentValues();
        value.put("city", city);
        value.put("temperature", temp);
        value.put("icon", iconURL);

        if (cs!= null && cs.getCount()>0){
            cs.moveToFirst();
            int cityID = cs.getInt(cs.getColumnIndexOrThrow("id"));
            db.update("location", value, "id=?", new String[]{String.valueOf(cityID)});
            Log.i("city update: ", city);
        }
        else {
            db.insert("location", null, value);
            Log.i("city add: ", city);
        }
        cs.close();
    }
}


