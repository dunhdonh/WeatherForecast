package com.example.weather_forecast.domains;

public class hourly {
    private String hour;
    private int temp;
    private String iconURL;

    public hourly(String hour, int temp, String iconURL) {
        this.hour = hour;
        this.temp = temp;
        this.iconURL = iconURL;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }


}
