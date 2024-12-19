package com.example.weather_forecast.domains;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingAPI {
    @GET("geo/1.0/direct")
    Call<List<geoLocation>> getLocations(
            @Query("q")String query,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );
}
