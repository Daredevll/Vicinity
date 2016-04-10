package com.vicinity.vicinity.controller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.NotificationActivity;
import com.vicinity.vicinity.utilities.CustomNotificationElement;
import com.vicinity.vicinity.utilities.ServerCommManager;

/**
 * Created by Jovch on 08-Apr-16.
 */
public class ReservationAnswerDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener{

    private static String customerId;
    private static String restaurantId;
    private static int peopleCount;
    private static String comment;
    private static String time;
    private static String date;
    private static String placeName;
    static CustomNotificationElement cne;





    // VIEWS AND CONTAINERS:

    TextView name, dateTv, timeTv, people, customerComment;
    static EditText placeComment;
    Button decline, confirm;

   public static ReservationAnswerDialog newInstance(CustomNotificationElement n){
        ReservationAnswerDialog dialog = new ReservationAnswerDialog();

       cne = n;

        Bundle args = new Bundle();
        args.putString("customer_id", n.getCustomerId());
        args.putString("place_id", n.getRestaurantId());
        args.putInt("people_count", n.getPeopleCount());
        args.putString("comment", n.getComment());
        args.putString("time", n.getTime());
        args.putString("date", n.getDate());
        args.putString("place_name", n.getPlaceName());

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerId = getArguments().getString("customer_id");
        restaurantId = getArguments().getString("place_id");
        peopleCount = getArguments().getInt("people_count");
        comment = getArguments().getString("comment");
        time = getArguments().getString("time");
        date = getArguments().getString("date");
        placeName = getArguments().getString("place_name");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_answer_reservation, container, false);

        name = (TextView) v.findViewById(R.id.dialog_answer_place_name);
        dateTv = (TextView) v.findViewById(R.id.dialog_answer_date_tv);
        timeTv = (TextView) v.findViewById(R.id.dialog_answer_time_tv);
        people = (TextView) v.findViewById(R.id.dialog_answer_people_count);
        customerComment = (TextView) v.findViewById(R.id.dialog_answer_comment_tv);
        placeComment = (EditText) v.findViewById(R.id.dialog_answer_business_comment_edit);
        decline = (Button) v.findViewById(R.id.dialog_answer_deny_button);
        confirm = (Button) v.findViewById(R.id.dialog_answer_confirm_button);

        name.setText(getString(R.string.new_reservation_for) + placeName +"'");
        dateTv.setText(date);
        timeTv.setText(time);
        people.setText(String.valueOf(peopleCount));
        customerComment.setText(comment);

        confirm.setOnClickListener(this);
        decline.setOnClickListener(this);

        return v;
    }






    private void openConfirmDialog(boolean confirm) {
        Log.e("Dialog", "Confirmed: " + confirm);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        ConfirmDialog cd = ConfirmDialog.newInstance(this, confirm);
        cd.show(ft, "RESERVEDIALOG");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_answer_confirm_button:
                openConfirmDialog(true);
                break;
            case R.id.dialog_answer_deny_button:
                openConfirmDialog(false);
                break;
        }
    }




    public static class ConfirmDialog extends android.support.v4.app.DialogFragment{

        private boolean isConfirm;
        private String placeCom;
        private static DialogFragment parentFr;

        public static ConfirmDialog newInstance(DialogFragment parent, boolean isConfirm){
            ConfirmDialog confirmDialog = new ConfirmDialog();

            parentFr = parent;
            Bundle args = new Bundle();
            args.putBoolean("is_confirm", isConfirm);
            args.putString("business_comment", placeComment.getText().toString());

            confirmDialog.setArguments(args);

            return confirmDialog;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            isConfirm = getArguments().getBoolean("is_confirm");
            placeCom = getArguments().getString("business_comment");
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_confirm, container, false);

            Button yes = (Button) v.findViewById(R.id.dialog_confirm_button_YES);
            Button no = (Button) v.findViewById(R.id.dialog_confirm_button_NO);
            TextView actionTv = (TextView) v.findViewById(R.id.dialog_confirm_action_type_tv);

            yes.setText(isConfirm ? getContext().getString(R.string.confirm) : getString(R.string.decline));
            actionTv.setText(isConfirm ? getString(R.string.confirm): getString(R.string.decline) );


            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServerCommManager.getInstance().onPostReservationAnswer(customerId, restaurantId, peopleCount, placeCom, time, date, isConfirm, placeName);
                    ((NotificationActivity) getActivity()).removeNotification(cne);
                    dismiss();
                    parentFr.dismiss();
                }
            });

            return v;
        }

    }
}

























