package com.usf.pickup.api.models;

import java.util.Date;

public class User {
    private String _id;
    private String email;
    private String displayName;
    private String profileDescription;
    private Date createdAt;
    private Date updatedAt;
    private String profilePicture;

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

    public String getProfileDescription() {return profileDescription;}

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }
}
