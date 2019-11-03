package com.usf.pickup.ui.profile;

import androidx.annotation.Nullable;

public class ProfileFormState {

    @Nullable
    private Integer nameError;
    @Nullable
    private Integer descriptionError;

    public ProfileFormState() {
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    public void setNameError(@Nullable Integer nameError) {
        this.nameError = nameError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(@Nullable Integer descriptionError) {
        this.descriptionError = descriptionError;
    }

    boolean isDataValid() {
        return this.nameError == null && this.descriptionError == null;
    }
}
