package com.vicinity.vicinity.controller.fragments;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.recycler.ResultsRecyclerAdapter;
import com.vicinity.vicinity.utilities.commmanagers.QueryProcessingManager;
import com.vicinity.vicinity.utilities.commmanagers.QueryProcessingManager.CustomPlace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment implements QueryProcessingManager.PlacesListRequester, View.OnClickListener{

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
    TextView noResultsText;
    ArrayList<Button> toggles;

    private Button sortByNameButton, sortByDistanceButton, sortByRatingButton, sortByOpenButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        placesList = new ArrayList<CustomPlace>();
        recView = (RecyclerView) rootView.findViewById(R.id.recycler_view_results_fragment);
        recAdapter = new ResultsRecyclerAdapter(getActivity(), placesList);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(recAdapter);
        noResultsText = (TextView) rootView.findViewById(R.id.results_fragment_no_results);
        sortByNameButton = (Button) rootView.findViewById(R.id.results_fragment_sort_name_button);
        sortByDistanceButton = (Button) rootView.findViewById(R.id.results_fragment_sort_distance_button);
        sortByRatingButton = (Button) rootView.findViewById(R.id.results_fragment_sort_rating_button);
        sortByOpenButton = (Button) rootView.findViewById(R.id.results_fragment_sort_open_button);
        toggles = new ArrayList<>();
        toggles.add(sortByDistanceButton);
        toggles.add(sortByNameButton);
        toggles.add(sortByOpenButton);
        toggles.add(sortByRatingButton);
        for (Button b: toggles){
            b.setOnClickListener(this);
            b.setTextColor(0x4AFFFFFF);
        }
        if (rListener.getSearchFlag() == null){
            sortByDistanceButton.setTextColor(0xFFFFFFFF);
        }
        else {
            restoreToggleLight(rListener.getSearchFlag());
        }

        /*
            This if-else checks if there's already loaded list of places and if so, it instantiates the adapter
            with it, without making new request. This
         */
        if (rListener.getCurrentSearchResults() == null || rListener.getCurrentSearchResults().isEmpty()) {
            // Initiates a query to Google, which dynamically callbacks to fill the recAdapter list with elements

            if (rListener.getCityName().isEmpty()){
                Log.e("DEBUG", "CITY NAME IN MAIN ACTIVITY = EMPTY");
                rListener.setCurrentLocation(rListener.getDetectedLocation());
                Log.e("DEBUG", "CURRENT USED LOCATION : " + rListener.getCurrentLocation().getLongitude() + " " + rListener.getCurrentLocation().getLatitude());
            }
            else {
                Log.e("DEBUG", "CITY NAME IN MAIN ACTIVITY = " + rListener.getCityName());
                rListener.setCurrentLocation(rListener.getCityLocation());
                Log.e("DEBUG", "CURRENT USED LOCATION : " + rListener.getCurrentLocation().getLongitude() + " " + rListener.getCurrentLocation().getLatitude());
            }

            QueryProcessingManager.getInstance().fillResultsList(this, rListener.getCurrentLocation(), rListener.getQueryType(), rListener.getSearchedPlaceName(),
                                                                rListener.isSortPopular(), rListener.searchByName());

            rListener.setCurrentSearchResults(placesList);

        }
        else {
            placesList.addAll(rListener.getCurrentSearchResults());
            recAdapter.notifyDataSetChanged();
        }

        // TODO: Make a query to Google with the params passed from the previous fragment
        // TODO: Process the returned result and inflate it to a RecyclerView
        // TODO: Create an UI with Tabs, switching between RecyclerView and Map, presenting the results
        // TODO: Create a mechanism passing the picked place placeID to the next Fragment

        return rootView;
    }


    private void restoreToggleLight(String searchFlag) {
        switch (searchFlag){
            case "NAME":
                sortByNameButton.setTextColor(0xFFFFFFFF);
                break;
            case "DISTANCE":
                sortByDistanceButton.setTextColor(0xFFFFFFFF);
                break;
            case "RATING":
                sortByRatingButton.setTextColor(0xFFFFFFFF);
                break;
            case "OPEN":
                sortByOpenButton.setTextColor(0xFFFFFFFF);
                break;
        }
    }

    @Override
    public void update(ArrayList<CustomPlace> places) {
        // Validate if no results found
        if (places == null || places.isEmpty()){
            showNoResultsFound();
        }

        placesList.clear();
        placesList.addAll(places);
        Log.e("URL", "places in fragment updated, recAdapter.notifyDataSetChanged() invoking");
        recAdapter.notifyDataSetChanged();
    }

    private void showNoResultsFound() {
        recView.setVisibility(View.INVISIBLE);
        noResultsText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        for (Button b: toggles){
            b.setTextColor(0x4AFFFFFF);
        }
        switch (v.getId()){
            case R.id.results_fragment_sort_name_button:
                Collections.sort(placesList, new Comparator<CustomPlace>() {
                    @Override
                    public int compare(CustomPlace lhs, CustomPlace rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                rListener.setSearchFlag("NAME");
                break;
            case R.id.results_fragment_sort_distance_button:
                Collections.sort(placesList, new Comparator<CustomPlace>() {
                    @Override
                    public int compare(CustomPlace lhs, CustomPlace rhs) {
                        return (Double.valueOf(lhs.getDistance().replace("km", "")).compareTo(Double.valueOf(rhs.getDistance().replace("km", ""))));
                    }
                });
                rListener.setSearchFlag("DISTANCE");
                break;
            case R.id.results_fragment_sort_rating_button:
                Collections.sort(placesList, new Comparator<CustomPlace>() {
                    @Override
                    public int compare(CustomPlace lhs, CustomPlace rhs) {
                        return (int) ((rhs.getRating() * 1000) - (lhs.getRating()*1000));
                    }
                });
                rListener.setSearchFlag("RATING");
                break;
            case R.id.results_fragment_sort_open_button:
                Collections.sort(placesList, new Comparator<CustomPlace>() {
                    @Override
                    public int compare(CustomPlace lhs, CustomPlace rhs) {
                        if (lhs.isOpenNow() && rhs.isOpenNow()){
                            return 0;
                        }
                        else if (!lhs.isOpenNow() && !rhs.isOpenNow()){
                            return 0;
                        }
                        else if (lhs.isOpenNow()){
                            return -11;
                        }
                        else{
                            return 1;
                        }
                    }
                });
                rListener.setSearchFlag("OPEN");
                break;
        }
        rListener.setCurrentSearchResults(placesList);
        recAdapter.notifyDataSetChanged();
        ((Button) v).setTextColor(0xFFFFFFFF);
    }


    public interface ResultsAndDetailsFragmentListener {
        Location getCurrentLocation();
        String getQueryType();
        void startDetailsFragment(CustomPlace place);
        CustomPlace getCurrentDetailPlace();
        void setCurrentSearchResults(ArrayList<CustomPlace> currentResults);
        ArrayList<CustomPlace> getCurrentSearchResults();
        Boolean isSortPopular();
        boolean searchByName();
        String getCityName();
        String getSearchedPlaceName();
        void setCurrentLocation(Location loc);
        Location getDetectedLocation();
        Location getCityLocation();
        String getSearchFlag();
        void setSearchFlag(String flag);

    }



}
