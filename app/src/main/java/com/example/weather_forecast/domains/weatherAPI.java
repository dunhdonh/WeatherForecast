package com.example.weather_forecast.domains;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface weatherAPI {
    @GET("data/2.5/weather")  // Endpoint của API
    Call<WeatherResponse> getWeather(

            @Query("q") String city,        // Tham số query (tên thành phố)
            @Query("appid") String apiKey,  // Tham số query (API key)
            @Query("units") String unit);


    @GET("data/2.5/forecast")
    Call<HourlyForecastResponse> getDailyForecast(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String unit);


}
