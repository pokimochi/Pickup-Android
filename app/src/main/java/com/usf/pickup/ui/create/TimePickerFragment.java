package com.usf.pickup.ui.create;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener listener;
    private Calendar initTime;

    public TimePickerFragment(Calendar initTime, TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
        this.initTime = initTime;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour = initTime.get(Calendar.HOUR_OF_DAY);
        int minute = initTime.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
}
