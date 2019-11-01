package com.usf.pickup;

import android.app.Application;

public class Pickup extends Application {
    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
