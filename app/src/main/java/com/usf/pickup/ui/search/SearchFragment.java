package com.usf.pickup.ui.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {
    private final String DIALOG_FRAGMENT_TAG = "com.usf.pickup.ui.search.dialog";
    private SearchViewModel searchViewModel;
    private FilterBottomSheetDialogFragment filterBottomSheetDialogFragment;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private int LOCATION_REQUEST_CODE = 2;
    private int FILTER_CLOSED_REQUEST_CODE = 3;
    private View root;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationSearchText;
    private Spinner sportSpinner;
    private SearchAdapter searchAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getContext(), getString(R.string.places_api_key));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        root = inflater.inflate(R.layout.fragment_search, container, false);

        locationSearchText = root.findViewById(R.id.locationSearch);
        ImageButton currentLocationButton = root.findViewById(R.id.currentLocationButton);
        ImageButton imageButton = root.findViewById(R.id.filter_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterOptions();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        sportSpinner = root.findViewById(R.id.search_spinner);

        searchViewModel.getAvailableSports().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            R.layout.spinner_text_color, strings.toArray(new String[0]));
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    sportSpinner.setAdapter(adapter);
                }
            }
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trySearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchViewModel.getLocationSelection().observe(this, new Observer<SearchViewModel.LocationSelection>() {
            @Override
            public void onChanged(SearchViewModel.LocationSelection locationSelection) {
                locationSearchText.setText(locationSelection.getName());
                trySearch();
            }
        });

        searchViewModel.getSearchResults().observe(this, new Observer<ApiResult<Game[]>>() {
            @Override
            public void onChanged(ApiResult<Game[]> apiResult) {
                if(apiResult.isSuccess()){
                    searchAdapter.updateResults(apiResult.getData());
                }
            }
        });

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.LAT_LNG);

        locationSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLocationPermission()){
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(checkLocationPermission()){
                                        gotLocation(location);
                                    }
                                }
                            });
                }
            }
        });

        searchAdapter = new SearchAdapter(root.getContext());
        ListView searchList = root.findViewById(R.id.search_list_view);
        searchList.setDivider(null);
        searchList.setDividerHeight(0);
        searchList.setAdapter(searchAdapter);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                searchViewModel.getLocationSelection().setValue(new SearchViewModel.LocationSelection(place.getLatLng(), place.getName()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if(requestCode == LOCATION_REQUEST_CODE){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(checkLocationPermission()){
                                gotLocation(location);
                            }
                        }
                    });
        }else if(requestCode == FILTER_CLOSED_REQUEST_CODE){
            searchViewModel.getHideFull().setValue(data.getBooleanExtra(FilterBottomSheetDialogFragment.HIDE_FULL, false));
            searchViewModel.getHideOngoing().setValue(data.getBooleanExtra(FilterBottomSheetDialogFragment.HIDE_ONGOING, false));
            searchViewModel.getMaxDistance().setValue(data.getStringExtra(FilterBottomSheetDialogFragment.MAX_DISTANCE));
            searchViewModel.getStartsBy().setValue(data.getStringExtra(FilterBottomSheetDialogFragment.STARTS_BY));
            trySearch();
        }
    }

    private void trySearch(){
        if(searchViewModel.getLocationSelection().getValue() != null && sportSpinner.getSelectedItem() != null){
            try {
                searchViewModel.search(sportSpinner.getSelectedItem().toString(), FilterBottomSheetDialogFragment.maxDistanceMap.get(searchViewModel.getMaxDistance().getValue()), searchViewModel.getLocationSelection().getValue().getLatLng(), searchViewModel.getHideFull().getValue(), searchViewModel.getHideOngoing().getValue(), FilterBottomSheetDialogFragment.startsByMap.get(searchViewModel.getStartsBy().getValue()).call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void gotLocation(Location location){
        searchViewModel.getLocationSelection().setValue(new SearchViewModel.LocationSelection(new LatLng(location.getLatitude(), location.getLongitude()), "Current Location"));
        trySearch();
    }

    private void openFilterOptions() {
        filterBottomSheetDialogFragment = new FilterBottomSheetDialogFragment(searchViewModel.getHideFull().getValue(), searchViewModel.getHideOngoing().getValue(), searchViewModel.getMaxDistance().getValue(), searchViewModel.getStartsBy().getValue());
        filterBottomSheetDialogFragment.setTargetFragment(this, FILTER_CLOSED_REQUEST_CODE);
        filterBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()), DIALOG_FRAGMENT_TAG);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNav bottomNav = ((BottomNav) getActivity());
        Objects.requireNonNull(Objects.requireNonNull(bottomNav).getSupportActionBar()).hide();
    }
}