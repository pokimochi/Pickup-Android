package com.usf.pickup.api.models;

public class Weather {
    private double temp;
    private int humidity;
    private String description;

    public Weather(double temp, int humidity, String description) {
        this.temp = temp;
        this.humidity = humidity;
        this.description = description;
    }

    public double gettemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }
}
