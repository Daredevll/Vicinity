package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.QueryProcessor.CustomPlace.Review;

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
        ReviewsViewHolder holder = new ReviewsViewHolder(row);
        // TODO: Implement onClickListener for the reviews to expand...
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        Review r = reviews.get(position);

        holder.getRating().setRating((float) r.getRating());
        holder.getAuthorName().setText(r.getAuthorName());
        holder.getAvatar().setImageResource(R.drawable.ic_person_white_48dp);  //TODO: Set real profile pictures if available
        holder.getComment().setText(r.getComment());
        holder.getTime().setText("21:12:12\n14:31");    //TODO: Parse from currentTimeMilis to readable DateTime
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
