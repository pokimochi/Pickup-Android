package com.usf.pickup.ui.create;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.usf.pickup.Pickup;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CreateViewModel extends AndroidViewModel {
    private MutableLiveData<List<String>> availableSports = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedStartTime = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedEndTime = new MutableLiveData<>();
    private MutableLiveData<Place> place = new MutableLiveData<>();
    private MutableLiveData<CreateFormState> createFormState = new MutableLiveData<>();
    private MutableLiveData<ApiResult<Game>> createResult = new MutableLiveData<>();

    public void createGame(final String title, final String sport, final int numberOfPlayers, final Place place, final String description, final Calendar selectedDate, final Calendar selectedStartTime, final Calendar selectedEndTime){
        Pickup pickup = getApplication();

        Calendar startTime = (Calendar) selectedDate.clone();
        Calendar endTime = (Calendar) selectedDate.clone();

        startTime.set(Calendar.HOUR_OF_DAY, selectedStartTime.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.MINUTE, selectedStartTime.get(Calendar.MINUTE));
        startTime.set(Calendar.SECOND, selectedStartTime.get(Calendar.SECOND));
        startTime.set(Calendar.MILLISECOND, selectedStartTime.get(Calendar.MILLISECOND));

        endTime.set(Calendar.HOUR_OF_DAY, selectedEndTime.get(Calendar.HOUR_OF_DAY));
        endTime.set(Calendar.MINUTE, selectedEndTime.get(Calendar.MINUTE));
        endTime.set(Calendar.SECOND, selectedEndTime.get(Calendar.SECOND));
        endTime.set(Calendar.MILLISECOND, selectedEndTime.get(Calendar.MILLISECOND));

        ApiClient.getInstance(pickup).createGame(pickup.getJwt(), title, sport, numberOfPlayers, place.getAddress(), place.getName(), place.getLatLng(), description, startTime, endTime, new ApiResult.Listener<Game>() {
            @Override
            public void onResponse(ApiResult<Game> response) {
                createResult.setValue(response);
            }
        });
    }

    private void initializeUserData() {
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

    public void createDataChanged(final String title, Object sport, final String players, final String description, final Place place){
        CreateFormState create = new CreateFormState();

        if (!isTitleValid(title)) {
            create.setTitleError(R.string.invalid_game_title);
        }

        if (!isSportValid(sport)) {
            create.setSportsError(R.string.invalid_game_sport);
        }

        if (!isTotalPlayersValid(players)) {
            create.setPlayersError(R.string.invalid_game_players);
        }

        if (!isDescriptionValid(description)) {
            create.setDescriptionError(R.string.invalid_game_description);
        }

        if (!isPlaceValid(place)) {
            create.setLocationError(R.string.invalid_game_location);
        }

        createFormState.setValue(create);
    }

    private boolean isTitleValid(String title) {
        return title != null && title.length() >= 1 && title.length() <= 140;
    }

    private boolean isSportValid(Object sport) {
        return sport != null;
    }

    private boolean isPlaceValid(Place place) {
        return place != null;
    }

    private boolean isTotalPlayersValid(String players) {
        try {
            int playersInt = Integer.parseInt(players);
            return playersInt >= 2 && playersInt <= 250;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDescriptionValid(String description) {
        return description != null && description.length() <= 258;
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

    public MutableLiveData<CreateFormState> getCreateFormState() {
        return createFormState;
    }

    public MutableLiveData<Place> getPlace() {
        return place;
    }

    public MutableLiveData<ApiResult<Game>> getCreateResult() {
        return createResult;
    }
}