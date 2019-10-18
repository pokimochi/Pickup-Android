package com.usf.pickup.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
