package com.usf.pickup.ui.search;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.usf.pickup.Pickup;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private MutableLiveData<List<String>> availableSports;

    private void initializeUserData() {
        availableSports = new MutableLiveData<>();
        availableSports.setValue(new ArrayList<String>());

        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).getSports(pickup.getJwt(), new ApiResult.Listener<List<String>>() {
            @Override
            public void onResponse(ApiResult<List<String>> response) {
                availableSports.setValue(response.getData());
            }
        });
    }

    public SearchViewModel(Application application) {
        super(application);
        initializeUserData();
    }

    public LiveData<List<String>> getAvailableSports() {
        return availableSports;
    }
}