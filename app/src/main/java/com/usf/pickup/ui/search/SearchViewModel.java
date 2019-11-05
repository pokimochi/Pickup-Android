package com.usf.pickup.ui.search;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.usf.pickup.Pickup;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private MutableLiveData<List<String>> availableSports;
    private MutableLiveData<LocationSelection> locationSelection = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideFull = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideOngoing = new MutableLiveData<>();
    private MutableLiveData<String> maxDistance = new MutableLiveData<>();
    private MutableLiveData<String> startsBy = new MutableLiveData<>();
    private MutableLiveData<ApiResult<Game[]>> searchResults = new MutableLiveData<>();

    private void initializeUserData() {
        availableSports = new MutableLiveData<>();
        availableSports.setValue(new ArrayList<String>());
        hideFull.setValue(false);
        hideOngoing.setValue(false);
        maxDistance.setValue((String )FilterBottomSheetDialogFragment.maxDistanceMap.keySet().toArray()[2]);
        startsBy.setValue((String )FilterBottomSheetDialogFragment.startsByMap.keySet().toArray()[2]);

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

    public void search(String sport, Integer maxDistance, LatLng latLng, boolean hideFull, boolean hideOngoing, Calendar startsBy){
        Pickup pickup = getApplication();

        ApiClient.getInstance(pickup).search(pickup.getJwt(), sport, maxDistance, latLng, hideFull, hideOngoing, startsBy, new ApiResult.Listener<Game[]>() {
            @Override
            public void onResponse(ApiResult<Game[]> response) {
                searchResults.setValue(response);
            }
        });
    }

    public LiveData<List<String>> getAvailableSports() {
        return availableSports;
    }

    public MutableLiveData<LocationSelection> getLocationSelection() {
        return locationSelection;
    }

    public MutableLiveData<Boolean> getHideFull() {
        return hideFull;
    }

    public MutableLiveData<Boolean> getHideOngoing() {
        return hideOngoing;
    }

    public MutableLiveData<String> getMaxDistance() {
        return maxDistance;
    }

    public MutableLiveData<String> getStartsBy() {
        return startsBy;
    }

    public MutableLiveData<ApiResult<Game[]>> getSearchResults() {
        return searchResults;
    }

    public static class LocationSelection{
        public LocationSelection(LatLng latLng, String name) {
            this.latLng = latLng;
            this.name = name;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public String getName() {
            return name;
        }

        private LatLng latLng;
        private String name;
    }
}