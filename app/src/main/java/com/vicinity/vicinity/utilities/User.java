package com.vicinity.vicinity.utilities;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Jovch on 13-Apr-16.
 */
public class User {

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
