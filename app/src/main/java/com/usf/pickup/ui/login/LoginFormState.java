package com.usf.pickup.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;

    public LoginFormState() {
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    public void setUsernameError(@Nullable Integer usernameError) {
        this.usernameError = usernameError;
    }

    public void setPasswordError(@Nullable Integer passwordError) {
        this.passwordError = passwordError;
    }

    boolean isDataValid() {
        return this.usernameError == null && this.passwordError == null;
    }
}
