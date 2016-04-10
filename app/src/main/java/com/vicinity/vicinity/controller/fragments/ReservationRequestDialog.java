package com.vicinity.vicinity.controller.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.ServerCommManager;

import java.util.Calendar;

/**
 * Created by Jovch on 07-Apr-16.
 */
public class ReservationRequestDialog extends DialogFragment{


    String customerId;
    String placeId;
    String placeName;
    int people;
    String comment;

    Button timePick, datePick;
    Button cancel;
    Button proceed;

    static TextView dateTv, timeTv;

    EditText peoplePick;
    EditText commentEdit;


    static ReservationRequestDialog newInstance(String customerId, String placeId, String placeName) {
        ReservationRequestDialog f = new ReservationRequestDialog();

        // Supplying customer_id, place_id and place_name
        Bundle args = new Bundle();
        args.putString("customer_id", customerId);
        args.putString("place_id", placeId);
        args.putString("place_name", placeName);
        f.setArguments(args);

        return f;
    }

    public ReservationRequestDialog(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerId = getArguments().getString("customer_id");
        placeId = getArguments().getString("place_id");
        placeName = getArguments().getString("place_name");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.dialog_new_reservation, container, false);

        timePick = (Button) v.findViewById(R.id.dialog_reserve_set_time_button);
        datePick = (Button) v.findViewById(R.id.dialog_reserve_set_date_button);
        dateTv = (TextView) v.findViewById(R.id.dialog_reserve_date_tv);
        timeTv = (TextView) v.findViewById(R.id.dialog_reserve_time_tv);
        proceed = (Button) v.findViewById(R.id.dialog_reserve_proceed);
        cancel = (Button) v.findViewById(R.id.dialog_reserve_cancel);
        peoplePick = (EditText) v.findViewById(R.id.dialog_reserve_people_number);
        commentEdit = (EditText) v.findViewById(R.id.dialog_reserve_comment);


        timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                people = Integer.valueOf(peoplePick.getText().toString());
                comment = commentEdit.getText().toString();
                if (people < 1){
                    Toast.makeText(getActivity(), R.string.set_people_at_least_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                ServerCommManager.getInstance().onPostReservationRequest(customerId, placeId, placeName, people, comment, dateTv.getText().toString(), timeTv.getText().toString());
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }


    /**
     * TimePickerFragment
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = (hourOfDay < 10) ? ("0" + hourOfDay) : String.valueOf(hourOfDay);
            String min = (minute < 10) ? ("0" + minute) : String.valueOf(minute);
            timeTv.setText(hour + ":" + min);
        }
    }


    /**
     * DatePickerFragment
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (dateBeforeToday(year, month, day)){
                Toast.makeText(getActivity(), R.string.can_not_go_back_in_time, Toast.LENGTH_SHORT).show();
            }
            else {
                dateTv.setText(day + "." + (month + 1) + "." + year + " /" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "/");
            }
        }

        /**
         * Checks if the passed date is before the current
         * @param year
         * @param month
         * @param day
         * @return true if passed date is before today
         */
        private boolean dateBeforeToday(int year, int month, int day) {
            Calendar now = Calendar.getInstance();
            int yNow = now.get(Calendar.YEAR);
            int mNow = now.get(Calendar.MONTH);
            int dNow = now.get(Calendar.DAY_OF_MONTH);

            if (yNow > year){
                return true;
            }
            else if (yNow < year){
                return false;
            }
            else {
                if (mNow > month){
                    return true;
                }
                else if (mNow < month){
                    return false;
                }
                else {
                    if (dNow > day){
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
    }

}
