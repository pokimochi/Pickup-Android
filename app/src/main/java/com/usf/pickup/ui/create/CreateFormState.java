package com.usf.pickup.ui.create;

import androidx.annotation.Nullable;

public class CreateFormState {
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer playersError;
    @Nullable
    private Integer descriptionError;
    @Nullable
    private Integer sportsError;
    @Nullable
    private Integer locationError;

    public CreateFormState() {
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    public void setTitleError(@Nullable Integer titleError) {
        this.titleError = titleError;
    }

    @Nullable
    public Integer getPlayersError() {
        return playersError;
    }

    public void setPlayersError(@Nullable Integer playersError) {
        this.playersError = playersError;
    }

    @Nullable
    public Integer getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(@Nullable Integer descriptionError) {
        this.descriptionError = descriptionError;
    }

    @Nullable
    public Integer getSportsError() {
        return sportsError;
    }

    public void setSportsError(@Nullable Integer sportsError) {
        this.sportsError = sportsError;
    }

    @Nullable
    public Integer getLocationError() {
        return locationError;
    }

    public void setLocationError(@Nullable Integer locationError) {
        this.locationError = locationError;
    }

    boolean isDataValid() {
        return this.titleError == null && this.sportsError == null && this.playersError == null &&
                this.descriptionError == null && this.locationError == null;
    }
}
