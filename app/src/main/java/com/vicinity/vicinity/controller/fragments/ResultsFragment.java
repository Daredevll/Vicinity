package com.vicinity.vicinity.controller.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vicinity.vicinity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment {


    public ResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);

        // TODO: Make a query to Google with the params passed from the previous fragment
        // TODO: Process the returned result and inflate it to a RecyclerView
        // TODO: Create an UI with Tabs, switching between RecyclerView and Map, presenting the results
        // TODO: Create a mechanism passing the picked place placeID to the next Fragment
    }

}
