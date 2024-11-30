package com.example.weather_forecast.domains;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface weatherAPI {
    @GET("data/2.5/weather")  // Endpoint của API
    Call<WeatherResponse> getWeather(
            @Query("q") String city,        // Tham số query (tên thành phố)
            @Query("appid") String apiKey   // Tham số query (API key)
    );
}
