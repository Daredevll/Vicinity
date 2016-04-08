package com.vicinity.vicinity.controller.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vicinity.vicinity.R;

import com.vicinity.vicinity.controller.controllersupport.recycler.ReviewsRecyclerAdapter;
import com.vicinity.vicinity.controller.fragments.ResultsFragment.*;

public class ReviewsFragment extends Fragment {

    ResultsAndDetailsFragmentListener parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = (ResultsAndDetailsFragmentListener) activity;
    }

    public ReviewsFragment() {
        // Required empty public constructor
    }

    RecyclerView reviewsRecycler;
    ReviewsRecyclerAdapter adapter;
    TextView noReviews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        noReviews = (TextView) view.findViewById(R.id.reviews_fragment_no_reviews);
        reviewsRecycler = (RecyclerView) view.findViewById(R.id.reviews_recycler);

        if (parentActivity.getCurrentDetailPlace().getReviews() == null){
            noReviews.setVisibility(View.VISIBLE);
            reviewsRecycler.setVisibility(View.GONE);
        }
        else {
            reviewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new ReviewsRecyclerAdapter(getActivity(), parentActivity.getCurrentDetailPlace().getReviews());
            reviewsRecycler.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
