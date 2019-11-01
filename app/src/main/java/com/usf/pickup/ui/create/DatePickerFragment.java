package com.usf.pickup.ui.create;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;
    private Calendar minDate;
    private Calendar initDate;

    public DatePickerFragment(Calendar initDate, Calendar minDate, DatePickerDialog.OnDateSetListener listener){
        this.initDate = initDate;
        this.minDate = minDate;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = initDate.get(Calendar.YEAR);
        int month = initDate.get(Calendar.MONTH);
        int day = initDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(getActivity(), listener, year, month, day);
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        return dialog;
    }
}
