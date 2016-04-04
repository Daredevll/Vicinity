package com.vicinity.vicinity.controller.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.recycler.ResultsRecyclerAdapter;
import com.vicinity.vicinity.utilities.QueryProcessor;
import com.vicinity.vicinity.utilities.QueryProcessor.CustomPlace;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment implements QueryProcessor.PlacesListRequester {

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        rListener = (ResultsAndDetailsFragmentListener) context;
    }

    ResultsAndDetailsFragmentListener rListener;

    RecyclerView recView;
    RecyclerView.Adapter recAdapter;
    ArrayList<CustomPlace> placesList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);


        // TODO: Create a RecyclerView
        // TODO: Create a ResultsRecyclerAdapter
        // TODO: Create a RecyclerView.ViewHolder / use Place objects /

        placesList = new ArrayList<CustomPlace>();
        recView = (RecyclerView) rootView.findViewById(R.id.recycler_view_results_fragment);
        recAdapter = new ResultsRecyclerAdapter(getActivity(), placesList);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(recAdapter);


        // Initiates a query to Google, which dynamically callbacks to fill the recAdapter list with elements
        QueryProcessor.getInstance().fillResultsList(this, rListener.getCurrentLocation(), rListener.getQueryType());


        // TODO: Make a query to Google with the params passed from the previous fragment
        // TODO: Process the returned result and inflate it to a RecyclerView
        // TODO: Create an UI with Tabs, switching between RecyclerView and Map, presenting the results
        // TODO: Create a mechanism passing the picked place placeID to the next Fragment

        return rootView;
    }

    @Override
    public void update(ArrayList<CustomPlace> places) {
        // TODO: Update the adapter:
        placesList.clear();
        placesList.addAll(places);
        Log.e("URL", "places in fragment updated, recAdapter.notifyDataSetChanged() invoking");
        recAdapter.notifyDataSetChanged();
    }


    public interface ResultsAndDetailsFragmentListener {
        Location getCurrentLocation();
        String getQueryType();
        void startDetailsFragment(CustomPlace place);
        CustomPlace getCurrentDetailPlace();
    }

}
