package com.example.weather_forecast.domains;
// RetrofitClient.java
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.openweathermap.org/";  // URL cơ sở của API
    private static Retrofit retrofit = null;

    // Phương thức khởi tạo Retrofit với Singleton
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Tùy chỉnh OkHttpClient nếu cần
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Khởi tạo Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)  // Gắn OkHttpClient vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create())  // Sử dụng Gson để chuyển đổi JSON
                    .build();
        }
        return retrofit;
    }
}

