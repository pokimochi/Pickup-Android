package com.usf.pickup;

import android.app.Application;

import com.usf.pickup.ui.login.LoggedInUserView;

public class Pickup extends Application {

    private LoggedInUserView userData;

    public void setLoggedInUserView(LoggedInUserView userData) {
        this.userData = userData;
    }

    public LoggedInUserView getUserData() {
        return this.userData;
    }
}
