package com.usf.pickup.api.models;

import java.util.Calendar;
import java.util.Date;

public class Game {
    private String _id;
    private String title;
    private String sport;
    private Integer numberOfPlayers;
    private String locationName;
    private Point location;
    private String address;
    private String description;
    private Date startTime;
    private Date endTime;
    private User organizer;
    private String[] users;
    private boolean hasJoined;
    private boolean isOrganizer;

    public String get_id() { return _id; }

    public String getTitle() {
        return title;
    }

    public String getSport() {
        return sport;
    }

    public Integer getNumberOfPlayers() {
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

    public User getOrganizer() {
        return organizer;
    }

    public boolean hasJoined() { return hasJoined; }

    public void setHasJoined(boolean hasJoined) { this.hasJoined = hasJoined; }

    public boolean isOrganizer() { return isOrganizer; }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }
}
