package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.CustomPlace.Review;

import java.util.ArrayList;

/**
 * Created by Jovch on 04-Apr-16.
 */
public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<ReviewsViewHolder>{

    Context context;
    ArrayList<Review> reviews;


    public ReviewsRecyclerAdapter(Context context, ArrayList<Review> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.reviews_rec_row, parent, false);
        final ReviewsViewHolder holder = new ReviewsViewHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        Review r = reviews.get(position);

        holder.getRating().setRating((float) r.getRating());
        holder.getAuthorName().setText(r.getAuthorName());
        if (r.getAvatar() != null){
            holder.getAvatar().setImageBitmap(r.getAvatar());
        }
        else {
            holder.getAvatar().setImageResource(R.drawable.ic_person_white_48dp);
        }
        holder.getComment().setText(r.getComment());
        String months;
        String days;
        long passedSeconds = (System.currentTimeMillis()/1000) - r.getTime();
        long passedDays = passedSeconds/86400;
        if (passedDays > 30){
            months = String.valueOf(passedDays/30) + " months and ";
            if (passedDays%30 != 0) {
                days = String.valueOf(passedDays % 30) + " days";
            }
            else {
                days = "";
                months = months.replace(" and ", "");
            }
        }
        else {
            days = String.valueOf(passedDays + " days");
            months = "";
        }
        holder.getTime().setText(months + days + " ago");
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
