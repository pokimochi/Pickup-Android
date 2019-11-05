package com.usf.pickup.api.models;

import java.util.Calendar;
import java.util.Date;

public class Game {
    private String title;
    private String sport;
    private String numberOfPlayers;
    private String locationName;
    private Point location;
    private String address;
    private String description;
    private Date startTime;
    private Date endTime;
    private String[] users;

    public String getTitle() {
        return title;
    }

    public String getSport() {
        return sport;
    }

    public String getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String getLocationName() {
        return locationName;
    }

    public Point getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Integer getPlayerCount() {return users.length + 1;}
}
