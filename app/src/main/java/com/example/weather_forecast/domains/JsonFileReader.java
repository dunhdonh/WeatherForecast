package com.example.weather_forecast.domains;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonFileReader {
    public static String loadJson(Context context, String filePath){
        String json = null;
        try{
            InputStream inputStream = context.getAssets().open(filePath);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static HourlyForecastResponse getHourlyForecastResponseFromJson (Context context, String filePath){
        String jsonString = loadJson(context, filePath);
        Gson gson = new Gson();
        return gson.fromJson(jsonString, HourlyForecastResponse.class);
    }
}
