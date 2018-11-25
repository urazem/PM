package com.nomercy.meetly;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener listener;
    private int hour, minute;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this.listener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        listener = ontime;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }

    public void setListener(final TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }
}
