package com.vicinity.vicinity.controller.controllersupport.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.fragments.ResultsFragment;
import com.vicinity.vicinity.utilities.CustomPlace;

import java.util.ArrayList;

/**
 * Created by Jovch on 28-Mar-16.
 */
public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsViewHolder> {

    Context context;
    ArrayList<CustomPlace> places;

    public ResultsRecyclerAdapter(Context context, ArrayList<CustomPlace> places){
        this.context = context;
        this.places = places;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(context);
        View view = lf.inflate(R.layout.results_fragment_rec_row, parent, false);
        final ResultsViewHolder holder = new ResultsViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DETAILSLOG", "onClick in RecAdapter called");
                ((ResultsFragment.ResultsAndDetailsFragmentListener)context).startDetailsFragment(places.get(holder.getAdapterPosition()));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        CustomPlace p = places.get(position);
        Context context = holder.getContext();

//        holder.getPic().setImageResource(R.drawable.def_place);
        holder.getName().setText(p.getName());
        holder.getAddress().setText(p.getVicinity());
        holder.getRating().setRating(p.getRating());
        holder.getOpenNow().setText(p.isOpenNow() ?context.getString(R.string.open) : context.getString(R.string.closed));
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
