package com.usf.pickup.data;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.usf.pickup.data.model.LoggedInUser;
import com.usf.pickup.helpers.LoginHelper;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        final String server = "10.0.2.2:8080";

        // TODO: authentication in try
        try {
            LoggedInUser user = new LoggedInUser(username,"Jane Doe");
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
