package com.vicinity.vicinity.utilities.commmanagers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.vicinity.vicinity.utilities.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pipi on 7.4.2016 г..
 */
public class GooglePictureDownloadManager {

    public interface IDownloadImageListener {
        void onImageDownloaded(Bitmap image);
    }

    public static final String SIZE_LARGE = "large";
    public static final String SIZE_SMALL = "small";
    public static final String SIZE_AVATAR = "avatar";


    private static final int MAX_WIDTH_LARGE = 488;
    private static final int MAX_HEIGHT_LARGE = 800;

    private static final int MAX_WIDTH_SMALL= 244;
    private static final int MAX_HEIGHT_SMALL = 400;

    private static final int MAX_WIDTH_AVATAR = 90;
    private static final int MAX_HEIGHT_AVATAR = 90;



//    private IDownloadImageListener caller;

    private static GooglePictureDownloadManager instance;

    public static GooglePictureDownloadManager getInstance(){
        if (instance == null){
            instance = new GooglePictureDownloadManager();
        }
        return instance;
    }

    private GooglePictureDownloadManager() {

    }

    public void downloadImage(String photoReference){
        String urlPath = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + MAX_WIDTH_LARGE +
                "&maxheight=" + MAX_HEIGHT_LARGE +
                "&photoreference=" + photoReference +
                "&key=" + Constants.getBrowserApiKey();

        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new AsyncTask<URL, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(URL... params) {
                try {
                    return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

//                GooglePictureDownloadManager.this.caller.onImageDownloaded(bitmap);
            }
        }.execute(url);
    }

    public void setImageAsync(String photoReference, final ImageView frame){
        String urlPath = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + MAX_WIDTH_LARGE +
                "&maxheight=" + MAX_HEIGHT_LARGE +
                "&photoreference=" + photoReference +
                "&key=" + Constants.getBrowserApiKey();

        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new AsyncTask<URL, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(URL... params) {
                try {
                    return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                frame.setImageBitmap(bitmap);
            }
        }.execute(url);
    }

    /**
     * Sets the passed ImageView with Bitmap, downloaded by the passed reference
     * @param photoReference
     * @param frame
     */
    public void setImage(Activity caller, String photoReference, final ImageView frame, boolean onWiFi){
        int wid;
        int hei;
        if (onWiFi){
            wid = MAX_WIDTH_LARGE;
            hei = MAX_HEIGHT_LARGE;
        }
        else {
            wid = MAX_WIDTH_SMALL;
            hei = MAX_HEIGHT_SMALL;
        }
        String urlPath = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + wid +
                "&maxheight=" + hei +
                "&photoreference=" + photoReference +
                "&key=" + Constants.getBrowserApiKey();

        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap img = null;
        try {
        img = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Bitmap finalImg = img;
        caller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frame.setImageBitmap(finalImg);
            }
        });

    }

    /**
     * Dowloads and sets a Picture from URL to a passed Bitmap
     * @param avatarURL
     */
    public void setBitmap(String avatarURL, final QueryProcessingManager.CustomPlace.Review review){
        URL url = null;
        try {
            url = new URL(("http:" + avatarURL).replace("photo.jpg", "s800-w80-h80/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new AsyncTask<URL, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(URL... params) {
                try {
                    return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                review.setBmp(bitmap);
            }
        }.execute(url);
    }

    /**
     * Sets the ImageViews from the passed List with Bitmaps, loaded from the passed references.
     * Note that this method loads the images consequently rather than simultaneously in order to
     * improve performance by downloading the "closer" images first.
     * @param frames
     * @param references
     */
    public void fillFramesWithPhotos(final Activity caller, final ArrayList<ImageView> frames, final ArrayList<String> references){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                /*
                        Checks if Wi-Fi is connected to decide whether to download large or small photos
                 */
                ConnectivityManager connManager = (ConnectivityManager) caller.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wiFiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


                for (int idx = 0; idx < references.size(); idx++){
                    setImage(caller, references.get(idx), frames.get(idx), wiFiInfo.isConnected());
                }

                return null;
            }
        }.execute();

    }

}
