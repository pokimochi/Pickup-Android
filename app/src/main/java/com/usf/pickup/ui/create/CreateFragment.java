package com.usf.pickup.ui.create;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.usf.pickup.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateFragment extends Fragment {
    private CreateViewModel createViewModel;
    private SimpleDateFormat dateformat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private SimpleDateFormat timeformat = new SimpleDateFormat("h:m a", Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getContext(), getString(R.string.places_api_key));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel =
                ViewModelProviders.of(this).get(CreateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_create, container, false);

        final Spinner sportSpinner = root.findViewById(R.id.sport_dropdown);
        final LinearLayout date = root.findViewById(R.id.dateLayout);
        final TextView dateTextView = root.findViewById(R.id.dateTextView);
        final LinearLayout startsAt = root.findViewById(R.id.startsAtLayout);
        final TextView startsAtTextView = root.findViewById(R.id.startsAtTextView);
        final LinearLayout endsAt = root.findViewById(R.id.endsAtLayout);
        final TextView endsAtTextView = root.findViewById(R.id.endsAtTextView);
        final AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        createViewModel.getAvailableSports().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, strings.toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sportSpinner.setAdapter(adapter);
                }
            }
        });

        createViewModel.getSelectedDate().observe(this, new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                dateTextView.setText(dateformat.format(calendar.getTime()));
            }
        });

        createViewModel.getSelectedStartTime().observe(this, new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                startsAtTextView.setText(timeformat.format(calendar.getTime()));
            }
        });

        createViewModel.getSelectedEndTime().observe(this, new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                endsAtTextView.setText(timeformat.format(calendar.getTime()));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment(createViewModel.getSelectedDate().getValue(), Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2);

                        createViewModel.getSelectedDate().setValue(calendar);
                    }
                });
                datePicker.show(getFragmentManager(), "datepicker");
            }
        });

        startsAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment(createViewModel.getSelectedStartTime().getValue(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        calendar.set(Calendar.MINUTE, i1);

                        createViewModel.getSelectedStartTime().setValue(calendar);
                    }
                });
                timePicker.show(getFragmentManager(), "timepicker");
            }
        });

        endsAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment(createViewModel.getSelectedEndTime().getValue(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        calendar.set(Calendar.MINUTE, i1);

                        createViewModel.getSelectedEndTime().setValue(calendar);
                    }
                });
                timePicker.show(getFragmentManager(), "timepicker");
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return root;
    }
}