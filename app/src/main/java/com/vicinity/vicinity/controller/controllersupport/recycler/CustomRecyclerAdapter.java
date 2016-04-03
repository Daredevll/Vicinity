package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.QueryProcessor.CustomPlace;

import java.util.ArrayList;

/**
 * Created by Jovch on 28-Mar-16.
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    Context context;
    ArrayList<CustomPlace> places;

    public CustomRecyclerAdapter(Context context, ArrayList<CustomPlace> places){
        this.context = context;
        this.places = places;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(context);
        View view = lf.inflate(R.layout.results_fragment_rec_row, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement logic for the view / go to DetailFragment, load with Given place /
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        CustomPlace p = places.get(position);
//        holder.getPic().setImageResource(R.drawable.def_place);
        holder.getName().setText(p.getName());
        holder.getAddress().setText(p.getVicinity());
        holder.getRating().setRating(p.getRating());
        holder.getOpenNow().setText(p.isOpenNow()?"Open":"Closed");
        holder.getEstimatedTime().setText(p.getEstimateTime());
        holder.getDistance().setText(p.getDistance());
        holder.getType().setText(p.getTypes()[0]);
        holder.getRatingText().setText(String.valueOf(p.getRating()));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
