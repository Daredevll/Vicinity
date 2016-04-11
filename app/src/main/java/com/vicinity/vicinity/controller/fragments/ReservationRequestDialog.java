package com.vicinity.vicinity.controller.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.vicinity.vicinity.utilities.interfaces.Blurable;

import java.util.Calendar;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Jovch on 07-Apr-16.
 */
public class ReservationRequestDialog extends DialogFragment implements Blurable, View.OnClickListener{


    static String customerId;
    static String placeId;
    static String placeName;
    static int people;
    static String comment;
    
    private int peopleCounter;

    Button timePick, datePick;
    Button cancel;
    Button proceed;
    Button incrementPeople;
    Button decrementPeople;


    static TextView dateTv, timeTv;

    TextView peoplePick;
    EditText commentEdit;

    View rootDialogView;


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
        rootDialogView = v;
        timePick = (Button) v.findViewById(R.id.dialog_reserve_set_time_button);
        datePick = (Button) v.findViewById(R.id.dialog_reserve_set_date_button);
        dateTv = (TextView) v.findViewById(R.id.dialog_reserve_date_tv);
        timeTv = (TextView) v.findViewById(R.id.dialog_reserve_time_tv);
        proceed = (Button) v.findViewById(R.id.dialog_reserve_proceed);
        cancel = (Button) v.findViewById(R.id.dialog_reserve_cancel);
        peoplePick = (TextView) v.findViewById(R.id.dialog_reserve_people_number);
        commentEdit = (EditText) v.findViewById(R.id.dialog_reserve_comment);
        peopleCounter = 1;

        incrementPeople = (Button) v.findViewById(R.id.dialog_reservation_people_increment_button);
        decrementPeople = (Button) v.findViewById(R.id.dialog_reservation_people_decrement_button);

        incrementPeople.setOnClickListener(this);
        decrementPeople.setOnClickListener(this);

        

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
                try {
                    people = Integer.valueOf(peoplePick.getText().toString());
                }
                catch (NumberFormatException e){
                    Toast.makeText(getActivity(), "You must set people to at least one", Toast.LENGTH_SHORT).show();
                    return;
                }
                comment = commentEdit.getText().toString();
                if (people < 1){
                    Toast.makeText(getActivity(), R.string.set_people_at_least_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (datePick.getText().equals("...") || timePick.getText().equals("...")){
                    Toast.makeText(getActivity(), "Please, select time and date", Toast.LENGTH_SHORT).show();
                    return;
                }
                openConfirmDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Blurry.delete(((Blurable) getActivity()).getRootView());
                dismiss();
            }
        });

//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x22ffffff));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.button_shape_light);
        Blurry.with(getActivity()).radius(25).sampling(2).onto(((Blurable) getActivity()).getRootView());
        getDialog().getWindow().setTitle(placeName);

        setCancelable(false);
        return v;
    }




    private void openConfirmDialog() {
        Log.e("Dialog", "Opening Reservation Confirmation dialog");

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        ConfirmRequestDialog crd = ConfirmRequestDialog.newInstance(this);
        crd.show(ft, "RESERVEREQUESTDIALOG");
    }

    @Override
    public ViewGroup getRootView() {
        return (ViewGroup) rootDialogView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_reservation_people_increment_button:
                if (peopleCounter > 249){
                    Toast.makeText(getActivity(), "Are you planning a 'Lager Beer Riot'?", Toast.LENGTH_SHORT).show();
                }
                else {
                    peoplePick.setText(String.valueOf(++peopleCounter));
                }
                break;
            case R.id.dialog_reservation_people_decrement_button:
                if (peopleCounter == 1){
                    peoplePick.setText(String.valueOf(peopleCounter));
                }
                else {
                    peoplePick.setText(String.valueOf(--peopleCounter));
                }
                break;
        }
    }


    /**
     *
     *      Dialog to yes/no confirm the action of placing a reservation
     */
    public static class ConfirmRequestDialog extends android.support.v4.app.DialogFragment{

//        private boolean isConfirm;
//        private String placeCom;
        private static DialogFragment parentFr;

        public static ConfirmRequestDialog newInstance(DialogFragment parent){
            ConfirmRequestDialog confirmDialog = new ConfirmRequestDialog();

            parentFr = parent;
            Bundle args = new Bundle();
//            args.putBoolean("is_confirm", isConfirm);
//            args.putString("business_comment", placeComment.getText().toString());

//            confirmDialog.setArguments(args);

            return confirmDialog;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_confirm, container, false);

            Button yes = (Button) v.findViewById(R.id.dialog_confirm_button_YES);
            Button no = (Button) v.findViewById(R.id.dialog_confirm_button_NO);
            TextView actionTv = (TextView) v.findViewById(R.id.dialog_confirm_action_type_tv);

            yes.setText("SEND");
            actionTv.setText("MAKE");


            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServerCommManager.getInstance().onPostReservationRequest(customerId, placeId, placeName, people, comment, dateTv.getText().toString(), timeTv.getText().toString());
                    dismiss();
                    parentFr.dismiss();
                    Blurry.delete(((Blurable) getActivity()).getRootView());
                    Toast.makeText(getActivity(), "Reservation request sent", Toast.LENGTH_SHORT).show();
                }
            });

            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.button_shape_light_solid);
            setCancelable(false);
            return v;

        }

    }



    @Override
    public void dismiss() {
        Blurry.delete(((Blurable)getActivity()).getRootView());
        super.dismiss();
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
                dateTv.setText(day + "." + (month + 1) + "." + year);
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
