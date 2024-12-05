package com.example.weather_forecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_forecast.R;
import com.example.weather_forecast.adapters.hourlyAdapters;
import com.example.weather_forecast.domains.NominatimSearchTask;
import com.example.weather_forecast.domains.RetrofitClient;
import com.example.weather_forecast.domains.WeatherResponse;
import com.example.weather_forecast.domains.hourly;
import com.example.weather_forecast.domains.weatherAPI;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    //search bar
    private AutoCompleteTextView searchAutoCompleteTextView;

    private ArrayAdapter<String> adapter;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);

        // Adapter để hiển thị danh sách gợi ý
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        searchAutoCompleteTextView.setAdapter(adapter);

        // Lắng nghe sự thay đổi trong AutoCompleteTextView
        searchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) { // Chỉ kiểm tra >= completionThreshold
                     searchLocation(s.toString());

                    adapter.notifyDataSetChanged();
                    searchAutoCompleteTextView.showDropDown();
                } else {
                    adapter.clear(); // Xóa kết quả khi người dùng xóa ký tự
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

    // Hàm tìm kiếm địa điểm
    private void searchLocation(String query) {
        Log.d("SearchQuery", "Đang tìm kiếm: " + query);

        new NominatimSearchTask(results -> {
            runOnUiThread(() -> {
                ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        results
                );
                searchAutoCompleteTextView.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();
                searchAutoCompleteTextView.showDropDown();

//                adapter.clear();  // Xóa dữ liệu cũ
//                if (results != null && !results.isEmpty()) {
//                    for (String result : results) {
//                        adapter.add(result);  // Thêm từng kết quả vào Adapter
//                        Log.d("AdapterContent", "Thêm kết quả vào Adapter: " + result);
//                    }
//                    adapter.notifyDataSetChanged();  // Cập nhật Adapter
//                    searchAutoCompleteTextView.showDropDown();  // Hiển thị dropdown
//                    Log.d("AdapterUpdate", "Số lượng phần tử trong Adapter: " + adapter.getCount());
//                } else {
//                    Log.d("AdapterUpdate", "Danh sách kết quả trống.");
//                }
            });

        }).execute(query);
    }
}