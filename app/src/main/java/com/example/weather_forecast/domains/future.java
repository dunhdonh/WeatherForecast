package com.example.weather_forecast.domains;

public class future {
    private String day;
    private String dayMonth;
    private String picPath;
    private String status;
    private int temp;


    public future(String day, String dayMonth, String picPath, String status, int temp) {
        this.day = day;
        this.dayMonth = dayMonth;
        this.picPath = picPath;
        this.status = status;
        this.temp = temp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getDayMonth() {
        return dayMonth;
    }

    public void setDayMonth(String dayMonth) {
        this.dayMonth = dayMonth;
    }
}
