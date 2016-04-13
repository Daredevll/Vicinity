package com.vicinity.vicinity.utilities;

import android.graphics.Bitmap;

import com.google.android.gms.location.places.Place;
import com.vicinity.vicinity.utilities.commmanagers.GooglePictureDownloadManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jovch on 13-Apr-16.
 */
public class CustomPlace {


    public class Review {

        double rating;
        String authorName;
        String language;
        String profilePhotoUrl;
        String comment;
        long time;
        Bitmap avatar;

        public Review(String authorName, double rating, long time) {
            this.authorName = authorName;
            this.rating = rating;
            this.time = time;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setProfilePhotoUrl(String profilePhotoUrl) {
            if (!profilePhotoUrl.isEmpty()) {
                GooglePictureDownloadManager.getInstance().setBitmap(profilePhotoUrl, this);
            }
            this.profilePhotoUrl = profilePhotoUrl;
        }

        public Bitmap getAvatar() {
            return avatar;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public double getRating() {
            return rating;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getLanguage() {
            return language;
        }

        public String getProfilePhotoUrl() {
            return profilePhotoUrl;
        }

        public String getComment() {
            return comment;
        }

        public long getTime() {
            return time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Review review = (Review) o;

            if (Double.compare(review.rating, rating) != 0) return false;
            if (time != review.time) return false;
            return authorName.equals(review.authorName);

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(rating);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + authorName.hashCode();
            result = 31 * result + (int) (time ^ (time >>> 32));
            return result;
        }

        public void setBmp(Bitmap bmp) {
            this.avatar = bmp;
        }
    }

    ArrayList<Review> reviews;

    ArrayList<String> photosRefs;

    double longitude;
    double latitude;

    Place realPlace;

    String icon;
    String id;
    String name;
    String placeId;
    String reference;
    String[] types;
    String vicinity;
    String distance;
    String estimateTime;

    String localPhoneNumber;
    String internationalPhoneNumber;
    String website;
    String utcOffset;


    // Optional:

    float rating;
    boolean openNow;

    public CustomPlace(double longitude, double latitude, String icon, String id, String name, String place_id, String reference, String[] types, String vicinity) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.placeId = place_id;
        this.reference = reference;
        this.types = types;
        this.vicinity = vicinity;
    }

    public void addDetails(String localPhone, String interPhone, String web, String utcOffset, List<Review> reviews) {
        this.localPhoneNumber = localPhone;
        this.internationalPhoneNumber = interPhone;
        this.website = web;
        this.utcOffset = utcOffset;

        for (Review r : reviews) {
            addReview(r);
        }
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getDistance() {
        return distance;
    }


    void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<Review>();
        }
        if (!reviews.contains(review)) {
            reviews.add(review);
        }
    }

    public void addPhotosRefs(ArrayList<String> photosRefs) {
        this.photosRefs = photosRefs;
    }

    public ArrayList<String> getPhotosRefs() {
        return this.photosRefs;
    }

    public ArrayList<Review> getReviews() {
        return this.reviews;
    }

    public void addRating(float rating) {
        this.rating = rating;
    }

    public void addOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getReference() {
        return reference;
    }

    public String[] getTypes() {
        return types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public float getRating() {
        return rating;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public String getEstimateTime() {
        return this.estimateTime;
    }

    public Place getRealPlace() {
        return realPlace;
    }

    public void setRealPlace(Place realPlace) {
        this.realPlace = realPlace;
    }

    public String getLocalPhoneNumber() {
        return localPhoneNumber;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getUtcOffset() {
        return utcOffset;
    }

}
