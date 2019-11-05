package com.usf.pickup.ui.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.usf.pickup.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class FilterBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static String HIDE_FULL = "com.usf.pickup.ui.search.FilterBottomSheetDialogFragment.HideFull";
    public static String HIDE_ONGOING = "com.usf.pickup.ui.search.FilterBottomSheetDialogFragment.HideOngoing";
    public static String MAX_DISTANCE = "com.usf.pickup.ui.search.FilterBottomSheetDialogFragment.MaxDistance";
    public static String STARTS_BY = "com.usf.pickup.ui.search.FilterBottomSheetDialogFragment.StartsBy";

    private boolean hideFull;
    private boolean hideOngoing;
    private String maxDistance;
    private String startsBy;

    private CheckBox hideFullCheckbox;
    private CheckBox hideOngoingCheckbox;
    private Spinner maxDistanceSpinner;
    private Spinner startsBySpinner;

    public static final HashMap<String, Integer> maxDistanceMap = new LinkedHashMap<String, Integer>(){{
        put("5 Miles", 5);
        put("10 Miles", 10);
        put("20 Miles", 20);
        put("50 Miles", 50);
    }};

    public static final HashMap<String, Callable<Calendar>> startsByMap = new LinkedHashMap<String, Callable<Calendar>>(){{
        put("1 Day", new Callable<Calendar>() {
            @Override
            public Calendar call() throws Exception {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                return calendar;
            }
        });
        put("1 Week", new Callable<Calendar>() {
            @Override
            public Calendar call() throws Exception {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 7);
                return calendar;
            }
        });
        put("1 Month", new Callable<Calendar>() {
            @Override
            public Calendar call() throws Exception {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                return calendar;
            }
        });
        put("1 Year", new Callable<Calendar>() {
            @Override
            public Calendar call() throws Exception {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 1);
                return calendar;
            }
        });
    }};

    public FilterBottomSheetDialogFragment(boolean hideFull, boolean hideOngoing, String maxDistance, String startsBy) {
        this.hideFull = hideFull;
        this.hideOngoing = hideOngoing;
        this.maxDistance = maxDistance;
        this.startsBy = startsBy;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_search, container, false);

        hideFullCheckbox = view.findViewById(R.id.full_checkbox);
        hideOngoingCheckbox = view.findViewById(R.id.ongoing_checkbox);
        maxDistanceSpinner = view.findViewById(R.id.max_distance_spinner);
        startsBySpinner = view.findViewById(R.id.starts_in_spinner);

        ArrayAdapter<String> maxDistAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, maxDistanceMap.keySet().toArray(new String[0]));
        maxDistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxDistanceSpinner.setAdapter(maxDistAdapter);
        maxDistanceSpinner.setSelection(maxDistAdapter.getPosition(maxDistance));

        ArrayAdapter<String> startsByAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, startsByMap.keySet().toArray(new String[0]));
        startsByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startsBySpinner.setAdapter(startsByAdapter);
        startsBySpinner.setSelection(startsByAdapter.getPosition(startsBy));

        hideFullCheckbox.setChecked(hideFull);
        hideOngoingCheckbox.setChecked(hideOngoing);

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Intent intent = new Intent();
        intent.putExtra(HIDE_FULL, hideFullCheckbox.isChecked());
        intent.putExtra(HIDE_ONGOING, hideOngoingCheckbox.isChecked());
        intent.putExtra(MAX_DISTANCE, maxDistanceSpinner.getSelectedItem().toString());
        intent.putExtra(STARTS_BY, startsBySpinner.getSelectedItem().toString());

        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);

        super.onDismiss(dialog);
    }
}
