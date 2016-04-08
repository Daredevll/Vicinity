package com.vicinity.vicinity.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pipi on 7.4.2016 г..
 */
public class GooglePictureDownloader {

    public interface IDownloadImageListener {
        void onImageDownloaded(Bitmap image);
    }

    private static final int MAX_WIDTH = 488;
    private static final int MAX_HEIGHT = 800;

    private IDownloadImageListener caller;

    public GooglePictureDownloader(IDownloadImageListener caller) {
        this.caller = caller;
    }

    public void downloadImage(String photoReference){
        String urlPath = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=" + MAX_WIDTH +
                "&maxheight=" + MAX_HEIGHT +
                "&photoreference=" + photoReference +
                "&key=" + Constants.BROWSER_API_KEY;

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

                GooglePictureDownloader.this.caller.onImageDownloaded(bitmap);
            }
        }.execute(url);
    }


}
