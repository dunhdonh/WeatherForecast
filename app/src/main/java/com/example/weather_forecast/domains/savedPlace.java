package com.example.weather_forecast.domains;

public class savedPlace {
    private String place;
    private int temp;
    private String icon;

    public savedPlace(String p, int t, String i){
        this.place = p;
        this.temp = t;

        this.icon = i;
    }
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
