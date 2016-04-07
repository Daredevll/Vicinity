package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;

import com.vicinity.vicinity.R;

/**
 * Created by Jovch on 07-Apr-16.
 */
public class NotificationsRecyclerViewHolder extends RecyclerView.ViewHolder{

    private TextView name;
    private TextView dateTime;
    private TextView people;
    private TextView confirm;
    private TextView comment;
    private Button dismiss;
    private Button answer;
    private Space buttonSpace;



    public NotificationsRecyclerViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.notification_row_place_name);
        dateTime = (TextView) itemView.findViewById(R.id.notification_row_date_time);
        people = (TextView) itemView.findViewById(R.id.notification_row_people);
        confirm = (TextView) itemView.findViewById(R.id.notification_row_status);
        comment = (TextView) itemView.findViewById(R.id.notification_row_comment);

        dismiss = (Button) itemView.findViewById(R.id.notification_row_button_dismiss);
        answer = (Button) itemView.findViewById(R.id.notification_row_button_answer);
        buttonSpace = (Space) itemView.findViewById(R.id.notification_row_interbutton_space);
    }


    public TextView getName() {
        return name;
    }

    public TextView getDateTime() {
        return dateTime;
    }

    public TextView getPeople() {
        return people;
    }

    public TextView getConfirm() {
        return confirm;
    }

    public TextView getComment() {
        return comment;
    }

    public Button getDismiss() {
        return dismiss;
    }

    public Button getAnswer() {
        return answer;
    }

    public Space getButtonSpace() {
        return buttonSpace;
    }
}
