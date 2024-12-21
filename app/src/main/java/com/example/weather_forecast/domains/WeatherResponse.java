package com.example.weather_forecast.domains;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WeatherResponse {
    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    public Wind getWind(){
        return wind;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public static class Main {
        @SerializedName("temp")
        private float temp;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("feels_like")
        private float feels_like;

        public float getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }

        public float getFeelsLike(){
            return feels_like;
        }
        @SerializedName("rain")
        private int rain;
        public int getRain(){
            return rain;
        }



    }

    public static class Weather {
        @SerializedName("main")
        private String main;

        public String getMain() {
            return main;
        }

        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }

        @SerializedName("icon")
        private String icon;

        public String getIcon(){
            return icon;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        public float getSpeed(){
            return speed;
        }
    }
    @SerializedName("pop")
    private float pop;

    public float getRainPercentage(){
        return pop;
    }
}
