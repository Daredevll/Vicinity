package com.vicinity.vicinity.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.exceptions.PrefsFieldNotFoundException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jovch on 25-Mar-16.
 */
public class DummyModelClass {

    public static class LoginManager {

        private static LoginManager instance;

        private GoogleApiClient googleApiClient;
        private GoogleSignInAccount googleSignInAccount;
        private boolean userLoggedIn;
        ArrayList<ShortPlace> currentBusinessOwnedPlaces;

        public static LoginManager getInstance() {
            if (instance == null) {
                instance = new LoginManager();
            }
            return instance;
        }

        private LoginManager() {

        }

        /**
         * Logs user to shared prefs
         *
         * @return
         */
        public boolean saveToPrefs(User user, Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();

            editor.putString("TOKEN_ID", user.getGoogleAccTokenId());
            editor.putString("ACC_ID", user.getGoogleAccId());
            editor.putString("GOOG_NAME", user.getGoogleName());
            editor.putString("GOOG_EMAIL", user.getGoogleEmail());

            editor.commit();
            return true;
        }

        private void setPrefsLogged(Context context, boolean loggedIn) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.putBoolean("USER_LOGGED", loggedIn);
            editor.commit();
        }


        public boolean checkLogged(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            if (sPref.getString("ACC_ID", defaultValue).equals(defaultValue)) {
                return false;
            }
            return true;
        }

        public String getLoggedUsername(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            return sPref.getString("GOOG_NAME", defaultValue);
        }

        public String getLoggedUserId(Context context) throws PrefsFieldNotFoundException {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String value = sPref.getString("ACC_ID", "defVal");
            if (value.equals("defVal")) {
                throw new PrefsFieldNotFoundException("The field 'ACC_ID' in prefs is empty or not existent");
            }
            return value;
        }

        public String getLoggedEmail(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String defaultValue = "defVal";
            return sPref.getString("GOOG_EMAIL", defaultValue);
        }

        public boolean clearSharedPrefs(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.clear();
            editor.commit();
            return true;
        }

        public boolean isUserLoggedIn(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            boolean defVal = false;
            return sPref.getBoolean("USER_LOGGED", defVal);
        }

        public void setGoogleApiClient(GoogleApiClient mGoogleApiClient) {
            this.googleApiClient = mGoogleApiClient;
        }

        public GoogleApiClient getGoogleApiClient() {
            return this.googleApiClient;
        }

        public void setGoogleSignInAccount(Context context, GoogleSignInAccount acc) {
            this.googleSignInAccount = acc;
            saveToPrefs(new User(acc), context);
            setPrefsLogged(context, true);
        }

        public GoogleSignInAccount getSignedInAccount() {
            return this.googleSignInAccount;
        }

        public void signOutAccount(Context context) {
            clearSharedPrefs(context);
            this.googleSignInAccount = null;
            setPrefsLogged(context, false);
            File bOwned = new File(Environment.getExternalStorageDirectory() + "/vicinityBusinessOwned");
            if (bOwned.exists()){
                bOwned.delete();
            }
        }

        public void setLoggedUserTypeBusiness(Context context, boolean businessAccount) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString(Constants.PREFS_USER_TYPE, businessAccount ? "true" : "false");
            editor.commit();
        }

        public boolean isLoggedUserTypeBusiness(Context context) {
            SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.prefs_file_key), Context.MODE_PRIVATE);
            String result = sPref.getString(Constants.PREFS_USER_TYPE, "defVal");
            if (result.equalsIgnoreCase("defVal")) {
                throw new PrefsFieldNotFoundException("The account type in prefs is empty or not existent");
            }
            return result.equalsIgnoreCase("true") ? true : false;
        }

        /**
         * Saves in a file the owned places of the current Business Account
         *
         * @param placesOwned
         */
        public void setBusinessUserOwnedPlaces(ArrayList<ShortPlace> placesOwned) {
            currentBusinessOwnedPlaces = placesOwned;
        }


        public ArrayList<ShortPlace> getBusinessUserOwnedPlaces() {
           return this.currentBusinessOwnedPlaces;
        }

        public static class User {

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
            boolean isBusiness;

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

            public boolean isBusiness() {
                return isBusiness;
            }


        }

        public static class Personal extends User {

            public Personal(GoogleSignInAccount account) {
                super(account);
            }

        }

        public static class Business extends User {

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
}
