package com.example.weather_forecast.domains;
import com.google.gson.annotations.SerializedName;

public class geoLocation {
    @SerializedName("name")
    private String name;

    @SerializedName("country")
    private String country;

    public String getName(){
        return name;
    }

    public String getCountry(){
        return country;
    }
}
