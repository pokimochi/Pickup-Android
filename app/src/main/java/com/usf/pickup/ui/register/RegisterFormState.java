package com.usf.pickup.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer displayNameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmPasswordError;

    public RegisterFormState() {
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    public void setEmailError(@Nullable Integer emailError) {
        this.emailError = emailError;
    }

    public void setPasswordError(@Nullable Integer passwordError) {
        this.passwordError = passwordError;
    }

    @Nullable
    public Integer getDisplayNameError() {
        return displayNameError;
    }

    public void setDisplayNameError(@Nullable Integer displayNameError) {
        this.displayNameError = displayNameError;
    }

    @Nullable
    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public void setConfirmPasswordError(@Nullable Integer confirmPasswordError) {
        this.confirmPasswordError = confirmPasswordError;
    }

    boolean isDataValid() {
        return this.emailError == null && this.passwordError == null &&
                this.displayNameError == null && this.confirmPasswordError == null;
    }
}
