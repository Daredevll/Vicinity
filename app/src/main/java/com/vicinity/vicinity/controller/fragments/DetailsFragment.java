package com.vicinity.vicinity.controller.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.fragments.ResultsFragment.ResultsAndDetailsFragmentListener;
import com.vicinity.vicinity.utilities.DummyModelClass;
import com.vicinity.vicinity.utilities.GooglePictureDownloader;
import com.vicinity.vicinity.utilities.QueryProcessor.CustomPlace;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsAndDetailsFragmentListener} interface
 * to handle interaction events.
 */
public class DetailsFragment extends Fragment implements OnMapReadyCallback, GooglePictureDownloader.IDownloadImageListener {

    private boolean reviewsActive;
    private ResultsAndDetailsFragmentListener dListener;
    private GoogleMap map;

    public boolean spin;


    // Containers and views:

    TextView ratingNumber;
    RatingBar ratingBar;
    ImageView addToFav;
    ImageView share;
    ImageView streetView;
    TextView name;
    Button website;
    Button dial;
    Button reserve;
    TextView address;
    TextView distanceAndEta;
    TextView workingTime;
    TextView openNow;

    LinearLayout photoWall;
    HorizontalScrollView wallScroller;

    CustomPlace currentPlace;

    GooglePictureDownloader pictureDownloader;

    FrameLayout fragmentHolder;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ResultsAndDetailsFragmentListener) {
            dListener = (ResultsAndDetailsFragmentListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_details, container, false);
        reviewsActive = true;

        currentPlace = dListener.getCurrentDetailPlace();

        ratingNumber = (TextView) root.findViewById(R.id.details_rating_tv);
        ratingBar = (RatingBar) root.findViewById(R.id.details_rating_rb);
        addToFav = (ImageView) root.findViewById(R.id.details_fav_img);
        share = (ImageView) root.findViewById(R.id.details_share_img);
        streetView = (ImageView) root.findViewById(R.id.details_streetview_img);
        name = (TextView) root.findViewById(R.id.details_name);
        website = (Button) root.findViewById(R.id.details_website_url);
        address = (TextView) root.findViewById(R.id.details_address);
        distanceAndEta = (TextView) root.findViewById(R.id.details_ETA);
        workingTime = (TextView) root.findViewById(R.id.details_working_time);
        openNow = (TextView) root.findViewById(R.id.details_open);
        reserve = (Button) root.findViewById(R.id.details_reserve_button);
        dial = (Button) root.findViewById(R.id.details_dial_button);
        photoWall = (LinearLayout) root.findViewById(R.id.details_photo_holder);
        wallScroller = (HorizontalScrollView) root.findViewById(R.id.details_photo_scroll_view);

        if (currentPlace.getPhotosRefs() != null){
            hangPhotos(currentPlace.getPhotosRefs());
            if (currentPlace.getPhotosRefs().size() > 1) {
                spin = true;
                startImageRoller();
            }
        }
        else {
            ImageView imgV = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imgV.setAdjustViewBounds(true);
            imgV.setLayoutParams(lp);
            imgV.setImageResource(R.drawable.default_photo_detail);
            photoWall.addView(imgV);
        }

        FrameLayout mapReviewsLayout = (FrameLayout) root.findViewById(R.id.details_map_reviews_frame);

        ratingNumber.setText(String.valueOf(currentPlace.getRating()));
        ratingBar.setRating(currentPlace.getRating());
        name.setText(currentPlace.getName());
        address.setText(currentPlace.getVicinity());
        distanceAndEta.setText(currentPlace.getDistance() + "     " + currentPlace.getEstimateTime());
        openNow.setText(currentPlace.isOpenNow() ? getContext().getString(R.string.open) : getContext().getString(R.string.closed));

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPlace.getWebsite() == null || currentPlace.getWebsite().isEmpty()) {
                    Toast.makeText(getActivity(), currentPlace.getName() + getContext().getString(R.string.has_not_set_website), Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = currentPlace.getWebsite();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });



        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragments();
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(getActivity().getApplicationContext(), new MyGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        mapReviewsLayout.setOnTouchListener(gestureListener);

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);

                ReservationRequestDialog rrd = ReservationRequestDialog.newInstance(DummyModelClass.LoginManager.getInstance()
                                .getLoggedUserId(getActivity()),
                                    dListener.getCurrentDetailPlace().getPlaceId(),
                                    dListener.getCurrentDetailPlace().getName());

                rrd.show(ft, "RESERVEDIALOG");


            }
        });

        // TODO: Add gesture to switch between Map/Reviews

        return root;
    }

    private void hangPhotos(ArrayList<String> photosRefs) {
        ArrayList<ImageView> frames = new ArrayList<>();
        for (String photoRef: photosRefs){
            ImageView imgV = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imgV.setAdjustViewBounds(true);
            imgV.setLayoutParams(lp);
            photoWall.addView(imgV);
            frames.add(imgV);
        }
        GooglePictureDownloader.getInstance().fillFramesWithPhotos(getActivity(), frames, photosRefs);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dListener = null;
    }

    private void switchFragments() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (reviewsActive) {
            SupportMapFragment mf = SupportMapFragment.newInstance();
            ft.replace(R.id.details_map_reviews_frame, mf);
            mf.getMapAsync(this);
        } else {
            ReviewsFragment rf = new ReviewsFragment();
            ft.replace(R.id.details_map_reviews_frame, rf);
        }

        ft.commit();
        reviewsActive = !reviewsActive;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double lat = dListener.getCurrentDetailPlace().getLatitude();
        double lon = dListener.getCurrentDetailPlace().getLongitude();
        LatLng markerPosition = new LatLng(lat, lon);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(markerPosition)
                .title(dListener.getCurrentDetailPlace().getName()));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
//        CameraUpdate pos = CameraUpdateFactory.newLatLng(markerPosition);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(markerPosition, 15);
        googleMap.animateCamera(zoom);
        googleMap.setTrafficEnabled(true);
        this.map = googleMap;
    }

    @Override
    public void onImageDownloaded(Bitmap image) {

    }

    private void startImageRoller() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int x = 2;
                while (spin){
                    if (!wallScroller.canScrollHorizontally(x)){
                        x = -x;
                    }
                    final int finalX = x;
                    wallScroller.scrollBy(finalX, 0);

                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;



        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //Left
                switchFragments();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //Right
                switchFragments();
            }

            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
