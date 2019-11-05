package com.usf.pickup.ui.create;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateFragment extends Fragment {
    private View root;
    private CreateViewModel createViewModel;
    private SimpleDateFormat dateformat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a", Locale.US);
    private int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getContext(), getString(R.string.places_api_key));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel =
                ViewModelProviders.of(this).get(CreateViewModel.class);
        root = inflater.inflate(R.layout.fragment_create, container, false);

        final Spinner sportSpinner = root.findViewById(R.id.sport_dropdown);
        final LinearLayout date = root.findViewById(R.id.dateLayout);
        final TextView dateTextView = root.findViewById(R.id.dateTextView);
        final LinearLayout startsAt = root.findViewById(R.id.startsAtLayout);
        final TextView startsAtTextView = root.findViewById(R.id.startsAtTextView);
        final LinearLayout endsAt = root.findViewById(R.id.endsAtLayout);
        final TextView endsAtTextView = root.findViewById(R.id.endsAtTextView);
        final TextView submitButton = root.findViewById(R.id.submit_button);
        final EditText titleEditText = root.findViewById(R.id.game_title_text_box);
        final EditText numberPlayersEditText = root.findViewById(R.id.numberPlayersEditText);
        final EditText descriptionEditText = root.findViewById(R.id.descriptionEditText);
        final TextView locationTextView = root.findViewById(R.id.locationTextView);
        final LinearLayout location = root.findViewById(R.id.locationLayout);

        createViewModel.getCreateFormState().observe(this, new Observer<CreateFormState>() {
            @Override
            public void onChanged(@Nullable CreateFormState createFormState) {
                if (createFormState == null) {
                    return;
                }
                submitButton.setEnabled(createFormState.isDataValid());

                // Clear existing errors
                titleEditText.setError(null);
                numberPlayersEditText.setError(null);
                descriptionEditText.setError(null);

                if (createFormState.getTitleError() != null) {
                    titleEditText.setError(getString(createFormState.getTitleError()));
                }
                if (createFormState.getPlayersError() != null) {
                    numberPlayersEditText.setError(getString(createFormState.getPlayersError()));
                }
                if (createFormState.getDescriptionError() != null) {
                    descriptionEditText.setError(getString(createFormState.getDescriptionError()));
                }
            }
        });

        createViewModel.getCreateResult().observe(this, new Observer<ApiResult<Game>>() {
            @Override
            public void onChanged(ApiResult<Game> gameApiResult) {
                if (gameApiResult == null) {
                    return;
                }

                if(gameApiResult.isSuccess()){
                    showCreateSuccess(gameApiResult.getData());

                    // Clear the form
                    titleEditText.setText("");
                    numberPlayersEditText.setText(R.string.default_players);
                    descriptionEditText.setText("");
                    createViewModel.getPlace().setValue(null);
                    createViewModel.getSelectedDate().setValue(Calendar.getInstance());
                    createViewModel.getSelectedStartTime().setValue(Calendar.getInstance());
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.HOUR_OF_DAY, 1);
                    createViewModel.getSelectedEndTime().setValue(end);
                    createViewModel.getCreateFormState().setValue(new CreateFormState());
                    submitButton.setEnabled(false);
                }else {
                    showCreateFailed(gameApiResult.getErrorMessage());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                createViewModel.createDataChanged(titleEditText.getText().toString(),
                        sportSpinner.getSelectedItem(),
                        numberPlayersEditText.getText().toString(),
                        descriptionEditText.getText().toString(),
                        createViewModel.getPlace().getValue());
            }
        };
        titleEditText.addTextChangedListener(afterTextChangedListener);
        numberPlayersEditText.addTextChangedListener(afterTextChangedListener);
        descriptionEditText.addTextChangedListener(afterTextChangedListener);

        createViewModel.getAvailableSports().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, strings.toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sportSpinner.setAdapter(adapter);
                    sportSpinner.setSelection(0);
                }

                if(createViewModel.getCreateFormState().getValue() != null && !createViewModel.getCreateFormState().getValue().isDataValid()){
                    createViewModel.createDataChanged(titleEditText.getText().toString(),
                            sportSpinner.getSelectedItem(),
                            numberPlayersEditText.getText().toString(),
                            descriptionEditText.getText().toString(),
                            createViewModel.getPlace().getValue());
                }
            }
        });

        createViewModel.getPlace().observe(this, new Observer<Place>() {
            @Override
            public void onChanged(Place place) {
                if(place != null){
                    locationTextView.setText(place.getName());
                }else {
                    locationTextView.setText(R.string.select_a_location);
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

        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.LAT_LNG);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createViewModel.createGame(titleEditText.getText().toString(),
                        sportSpinner.getSelectedItem().toString(),
                        Integer.valueOf(numberPlayersEditText.getText().toString()),
                        createViewModel.getPlace().getValue(),
                        descriptionEditText.getText().toString(),
                        createViewModel.getSelectedDate().getValue(),
                        createViewModel.getSelectedStartTime().getValue(),
                        createViewModel.getSelectedEndTime().getValue());
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Spinner sportSpinner = root.findViewById(R.id.sport_dropdown);
        final EditText titleEditText = root.findViewById(R.id.game_title_text_box);
        final EditText numberPlayersEditText = root.findViewById(R.id.numberPlayersEditText);
        final EditText descriptionEditText = root.findViewById(R.id.descriptionEditText);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                createViewModel.getPlace().setValue(place);
                createViewModel.createDataChanged(titleEditText.getText().toString(),
                        sportSpinner.getSelectedItem(),
                        numberPlayersEditText.getText().toString(),
                        descriptionEditText.getText().toString(),
                        createViewModel.getPlace().getValue());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showCreateSuccess(Game game){
        Toast.makeText(getContext(), String.format("Game \"%s\" successfully created", game.getTitle()), Toast.LENGTH_SHORT).show();
    }

    private void showCreateFailed(String errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((BottomNav) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}