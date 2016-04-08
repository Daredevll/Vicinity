package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.CustomNotificationElement;

import java.util.ArrayList;

/**
 * Created by Jovch on 07-Apr-16.
 */
public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewHolder>{

    NotificationsListActivity triggerActivity;
    ArrayList<CustomNotificationElement> notifications;

    public NotificationsRecyclerAdapter(Context context, ArrayList<CustomNotificationElement> notifications){
        this.triggerActivity = (NotificationsListActivity) context;
        this.notifications = notifications;
    }

    @Override
    public NotificationsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((Context)triggerActivity);
        View row = inflater.inflate(R.layout.notifications_rec_view_row, parent, false);
        //TODO: Implement onClickListener to open DetailsFragment with details of the place from the current notification
        return new NotificationsRecyclerViewHolder(row);
    }

    @Override
    public void onBindViewHolder(NotificationsRecyclerViewHolder holder, int position) {
        final CustomNotificationElement element = notifications.get(position);

        holder.getName().setText(element.getPlaceName());
        holder.getDateTime().setText(element.getDate() + "  -  " + element.getTime());
        holder.getPeople().setText(element.getPeopleCount() + " person/s/");
        holder.getConfirm().setText(element.isConfirmed() ? "Reservation Confirmed" : "Reservation Declined");
        holder.getComment().setText("Comment: " + element.getComment());
        holder.getDismiss().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifications.remove(element);
                notifyDataSetChanged();
            }
        });

        if (element.isAnswer()){
            holder.getButtonSpace().setVisibility(View.GONE);
            holder.getAnswer().setVisibility(View.GONE);
        }
        else {
            holder.getDismiss().setVisibility(View.GONE);
            holder.getButtonSpace().setVisibility(View.GONE);
            holder.getConfirm().setVisibility(View.INVISIBLE);
            holder.getAnswer().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    triggerActivity.triggerAnswer(element);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface NotificationsListActivity{
        void triggerAnswer(CustomNotificationElement notificationToBeAnswered);
    }
}
