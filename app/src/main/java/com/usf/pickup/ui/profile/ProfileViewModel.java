package com.usf.pickup.ui.profile;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.usf.pickup.Pickup;
import com.usf.pickup.ui.login.LoggedInUserView;

public class ProfileViewModel extends AndroidViewModel {

    final private LoggedInUserView userData = ((Pickup) getApplication()).getUserData();
    private MutableLiveData<String> displayName;
    private MutableLiveData<Uri> profilePicturePath;
    private int gamesPlayed;
    private MutableLiveData<String> profileDetail;

    private void initializeUserData() {
        displayName = new MutableLiveData<>();
        displayName.setValue(userData.getDisplayName());

        // Get and set profile picture uri from DB here
    }

    public ProfileViewModel(Application application) {
        super(application);
        initializeUserData();
    }

    public LiveData<String> getDisplayName() {
        return displayName;
    }

    public LiveData<Uri> getProfilePicturePath() {
        return profilePicturePath;
    }

}