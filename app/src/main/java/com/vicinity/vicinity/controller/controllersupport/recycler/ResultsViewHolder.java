package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vicinity.vicinity.R;

/**
 * Created by Jovch on 28-Mar-16.
 */
public class ResultsViewHolder extends RecyclerView.ViewHolder {

    private ImageView pic;
    private TextView name;
    private TextView address;
    private TextView distance;
    private TextView estimatedTime;
    private TextView openNow;
    private RatingBar rating;
    private TextView type;
    private TextView ratingText;


    public ResultsViewHolder(View itemView) {
        super(itemView);

        pic = (ImageView) itemView.findViewById(R.id.row_img);
        name = (TextView) itemView.findViewById(R.id.row_name_tv);
        address = (TextView) itemView.findViewById(R.id.row_address_tv);
        distance = (TextView) itemView.findViewById(R.id.row_distance_tv);
        estimatedTime = (TextView) itemView.findViewById(R.id.row_eta_tv);
        openNow = (TextView) itemView.findViewById(R.id.row_open_tv);
        rating = (RatingBar) itemView.findViewById(R.id.row_rating_rb);
        type = (TextView) itemView.findViewById(R.id.row_type_tv);
        ratingText = (TextView) itemView.findViewById(R.id.row_rating_tv);

    }


    public ImageView getPic() {
        return pic;
    }

    public TextView getName() {
        return name;
    }

    public TextView getAddress() {
        return address;
    }

    public TextView getEstimatedTime() {
        return estimatedTime;
    }

    public TextView getOpenNow() {
        return openNow;
    }

    public RatingBar getRating() {
        return rating;
    }

    public TextView getType() {
        return type;
    }

    public TextView getRatingText() {
        return ratingText;
    }

    public TextView getDistance() {
        return distance;
    }
}
