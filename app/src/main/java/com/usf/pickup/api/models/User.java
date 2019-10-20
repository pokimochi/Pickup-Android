package com.usf.pickup.api.models;

import java.util.Date;

public class User {
    private String _id;
    private String email;
    private String displayName;
    private Date createdAt;
    private Date updatedAt;

    public User() {
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
