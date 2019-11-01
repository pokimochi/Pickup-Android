package com.usf.pickup.api.models;

public class Point {
    public double[] getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

    public Point() {
    }

    private double[] coordinates;
    private  String type;
}
