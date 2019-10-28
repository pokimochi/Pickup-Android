package com.usf.pickup.ui.create;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.usf.pickup.Pickup;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CreateViewModel extends AndroidViewModel {

    private MutableLiveData<List<String>> availableSports;
    private MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedStartTime = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedEndTime = new MutableLiveData<>();

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

        selectedDate.setValue(Calendar.getInstance());
        selectedStartTime.setValue(Calendar.getInstance());

        // Default end time of 1 hour after now
        Calendar end = Calendar.getInstance();
        end.add(Calendar.HOUR_OF_DAY, 1);

        selectedEndTime.setValue(end);
    }

    public CreateViewModel(Application application) {
        super(application);
        initializeUserData();
    }

    public MutableLiveData<List<String>> getAvailableSports() {
        return availableSports;
    }

    public MutableLiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    public MutableLiveData<Calendar> getSelectedStartTime() {
        return selectedStartTime;
    }

    public MutableLiveData<Calendar> getSelectedEndTime() {
        return selectedEndTime;
    }
}