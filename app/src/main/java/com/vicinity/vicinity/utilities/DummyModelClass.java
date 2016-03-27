package com.vicinity.vicinity.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vicinity.vicinity.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jovch on 25-Mar-16.
 */
public class DummyModelClass {

    public static class LoginManager{

        private static LoginManager instance;

        private GoogleApiClient googleApiClient;
        private GoogleSignInAccount googleSignInAccount;
        private boolean userLoggedIn;

        public static LoginManager getInstance(){
            if (instance == null){
                instance = new LoginManager();
            }
            return instance;
        }

        private LoginManager(){

        }

        /**
         * Logs user to shared prefs
         * @return
         */
        public boolean saveToPrefs(User user, Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();

            editor.putString("TOKEN_ID", user.getGoogleAccTokenId());
            editor.putString("ACC_ID", user.getGoogleAccId());
            editor.putString("GOOG_NAME", user.getGoogleName());
            editor.putString("GOOG_EMAIL", user.getGoogleEmail());

            Set<String> set = new HashSet<>();

            if (user instanceof Personal){
                set.addAll(((Personal) user).lastVisitedPlaces);
                editor.putStringSet("LAST_VISITED", set);
                set.clear();
                set.addAll(((Personal) user).favouritePlaces);
                editor.putStringSet("FAV_PLACES", set);
            }
            else if (user instanceof Business){
                set.addAll(((Business) user).getOwnedPlaces());
                editor.putStringSet("BUSINESS_OWNED", set);
                editor.putBoolean("IS_ACTIVE", ((Business) user).isActive());
            }
            editor.commit();
            return true;
        }

        private void setPrefsLogged(Context context, boolean loggedIn){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.putBoolean("USER_LOGGED", loggedIn);
            editor.commit();
        }


        public boolean checkLogged(Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            if (sPref.getString("ACC_ID", defaultValue).equals(defaultValue)){
                return false;
            }
            return true;
        }

        public String getLoggedUsername(Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            return sPref.getString("GOOG_NAME", defaultValue);
        }

        public String getLoggedEmail(Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            return sPref.getString("GOOG_EMAIL", defaultValue);
        }

        public boolean clearSharedPrefs(Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.clear();
            editor.commit();
            return true;
        }

        public boolean isUserLoggedIn(Context context){
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            boolean defVal = false;
            return sPref.getBoolean("USER_LOGGED", defVal);
        }

        public void setGoogleApiClient(GoogleApiClient mGoogleApiClient) {
            this.googleApiClient = mGoogleApiClient;
        }

        public GoogleApiClient getGoogleApiClient(){
            return this.googleApiClient;
        }

        public void setGoogleSignInAccount(Context context, GoogleSignInAccount acc){
            this.googleSignInAccount = acc;
            saveToPrefs(new Personal(acc), context);
            setPrefsLogged(context, true);
        }

        public GoogleSignInAccount getSignedInAccount(){
            return this.googleSignInAccount;
        }

        public void signOutAccount(Context context) {
            clearSharedPrefs(context);
            this.googleSignInAccount = null;
            setPrefsLogged(context, false);
        }
    }

    public static abstract class User{

        public User(GoogleSignInAccount account) {
            this.googleAccTokenId = account.getIdToken();
            this.googleAccId = account.getId();
            this.googleName = account.getDisplayName();
            this.googleEmail = account.getEmail();
        }

        final String googleAccTokenId;
        final String googleAccId;
        final String googleName;
        String googleEmail;

        public String getGoogleEmail() {
            return googleEmail;
        }

        public String getGoogleAccTokenId() {
            return googleAccTokenId;
        }

        public String getGoogleAccId() {
            return googleAccId;
        }

        public String getGoogleName() {
            return googleName;
        }


    }

    public static class Personal extends User{
        private ArrayList<String> lastVisitedPlaces;
        private ArrayList<String> favouritePlaces;


        public ArrayList<String> getFavouritePlaces() {
            return favouritePlaces;
        }

        public ArrayList<String> getLastVisitedPlaces() {
            return lastVisitedPlaces;
        }

        public Personal(GoogleSignInAccount account) {
            super(account);
            lastVisitedPlaces = new ArrayList<>();
            favouritePlaces = new ArrayList<>();
        }

    }

    public static class Business extends User{

        private ArrayList<String> ownedPlaces;
        private boolean active;

        public Business(GoogleSignInAccount account, boolean active) {
            super(account);
//            ownedPlaces = GetOwnedPlacesFromGoogle(account); //TODO: Figure out how to retreive claimed businesses by user ID from Google
            this.active = active;
            ownedPlaces = new ArrayList<>();
        }

        public ArrayList<String> getOwnedPlaces() {
            return ownedPlaces;
        }

        public boolean isActive() {
            return active;
        }

    }
}
