package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.CustomNotificationElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        Context context = holder.getContext();

        holder.getName().setText(element.getPlaceName());
        holder.getDateTime().setText(element.getDate() + "  -  " + element.getTime());
        holder.getPeople().setText(element.getPeopleCount() + context.getString(R.string.person_persons));
        holder.getConfirm().setText(element.isConfirmed() ? context.getString(R.string.reservation_confirmed) : context.getString(R.string.reservation_declined));
        holder.getComment().setText(context.getText(R.string.comment) + element.getComment());
        holder.getDismiss().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifications.remove(element);
                final File f =  new File(Environment.getExternalStorageDirectory() + "/vicinityNotifsCacheClient");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileOutputStream fos = new FileOutputStream(f);
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(notifications);

                            fos.close();
                            oos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
