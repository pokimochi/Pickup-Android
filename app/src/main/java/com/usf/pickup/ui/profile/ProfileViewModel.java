package com.usf.pickup.ui.profile;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.usf.pickup.Pickup;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.MyGames;
import com.usf.pickup.api.models.User;

import java.util.regex.Pattern;

public class ProfileViewModel extends AndroidViewModel {
    private MutableLiveData<User> user;
    private MutableLiveData<ProfileFormState> profileFormState = new MutableLiveData<>();
    private MutableLiveData<ApiResult<MyGames>> myGames = new MutableLiveData<>();

    private void initializeUserData() {
        user = new MutableLiveData<>();

        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).getMyUser(pickup.getJwt(), new ApiResult.Listener<User>() {
            @Override
            public void onResponse(ApiResult<User> response) {
                user.setValue(response.getData());
            }
        });
    }

    public ProfileViewModel(Application application) {
        super(application);
        initializeUserData();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void profileDataChanged(String displayName, String description){
        ProfileFormState profile = new ProfileFormState();

        if (!isDisplayNameValid(displayName)) {
            profile.setNameError(R.string.invalid_display_name);
        }

        if (!isDescriptionValid(description)) {
            profile.setDescriptionError(R.string.invalid_description);
        }

        profileFormState.setValue(profile);
    }

    private boolean isDisplayNameValid(String displayName) {
        return displayName != null && displayName.length() >= 2 && displayName.length() <= 30 &&
                Pattern.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", displayName);
    }

    private boolean isDescriptionValid(String description) {
        return description != null && description.length() >= 1 && description.length() <= 280;
    }

    public void updateDisplayName(String displayName) {
        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).updateDisplayName(pickup.getJwt(), displayName, new ApiResult.Listener<User>() {
            @Override
            public void onResponse(ApiResult<User> response) {
                User u = user.getValue() != null ? user.getValue() : response.getData();
                u.setDisplayName(response.getData().getDisplayName());

                user.setValue(u);
            }
        });
    }

    public void updateProfileDescription(String profileDescription) {
        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).updateProfileDescription(pickup.getJwt(), profileDescription, new ApiResult.Listener<User>() {
            @Override
            public void onResponse(ApiResult<User> response) {
                User u = user.getValue() != null ? user.getValue() : response.getData();
                u.setProfileDescription(response.getData().getProfileDescription());

                user.setValue(u);
            }
        });
    }

    public void updateProfilePicture(byte[] image) {
        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).uploadProfilePicture(pickup.getJwt(), image, new ApiResult.Listener<User>() {
            @Override
            public void onResponse(ApiResult<User> response) {
                User u = user.getValue() != null ? user.getValue() : response.getData();
                u.setProfilePicture(response.getData().getProfilePicture());

                user.setValue(u);
            }
        });
    }

    public void searchMyGames() {
        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).getMyGames(pickup.getJwt(), new ApiResult.Listener<MyGames>() {
            @Override
            public void onResponse(ApiResult<MyGames> response) {
                myGames.setValue(response);
            }
        });
    }

    public MutableLiveData<ApiResult<MyGames>> getMyGames() {
        return myGames;
    }

    public MutableLiveData<ProfileFormState> getProfileFormState() {
        return profileFormState;
    }
}