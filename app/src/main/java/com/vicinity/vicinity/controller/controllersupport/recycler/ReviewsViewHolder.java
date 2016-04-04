package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vicinity.vicinity.R;

/**
 * Created by Jovch on 04-Apr-16.
 */
public class ReviewsViewHolder extends RecyclerView.ViewHolder{

    private RatingBar rating;
    private TextView authorName;
    private ImageView avatar;
    private TextView comment;
    private TextView time;


    public ReviewsViewHolder(View itemView) {
        super(itemView);

        setRating((RatingBar) itemView.findViewById(R.id.review_row_ratingbar));
        setAuthorName((TextView) itemView.findViewById(R.id.review_row_author_name));
        setAvatar((ImageView) itemView.findViewById(R.id.review_row_avatar));
        setComment((TextView) itemView.findViewById(R.id.review_comment_text));
        setTime((TextView) itemView.findViewById(R.id.review_comment_time));
    }


    public RatingBar getRating() {
        return rating;
    }

    public TextView getAuthorName() {
        return authorName;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public TextView getComment() {
        return comment;
    }

    public TextView getTime() {
        return time;
    }

    public void setRating(RatingBar rating) {
        this.rating = rating;
    }

    public void setAuthorName(TextView authorName) {
        this.authorName = authorName;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public void setComment(TextView comment) {
        this.comment = comment;
    }

    public void setTime(TextView time) {
        this.time = time;
    }
}
