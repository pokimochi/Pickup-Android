package com.usf.pickup.ui.profile;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.usf.pickup.Pickup;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.User;

public class ProfileViewModel extends AndroidViewModel {
    private MutableLiveData<User> user;

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
}