package com.vicinity.vicinity.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.AppearanceManager;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.commmanagers.LocalCommManager;
import com.vicinity.vicinity.utilities.commmanagers.ServerCommManager;
import com.vicinity.vicinity.utilities.ShortPlace;

import org.json.JSONObject;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ServerCommManager.RequestSenderContext {


    private static final int RC_SIGN_IN = 123;

    SignInButton signIn;

    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    LinearLayout userWelcomeBox;
    TextView userWelcomeText;
    RelativeLayout rootView;
    JSONObject loginResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /*
            Initializes the Constants Class with properties saved in the Prefs, needed for the normal operation of the Class
         */
        Constants.initialize(this);

        rootView = (RelativeLayout) findViewById(R.id.intro_root_layout);
        rootView.setBackgroundResource(AppearanceManager.getBackgroundID());

        userWelcomeBox = (LinearLayout) findViewById(R.id.welcome_box);
        userWelcomeText = (TextView) findViewById(R.id.user_welcome_text);
        signIn = (SignInButton) findViewById(R.id.google_sign_in);

        signIn.setVisibility(View.INVISIBLE);
        userWelcomeBox.setVisibility(View.INVISIBLE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }

            private void signIn() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        switchBoxes(LocalCommManager.LoginManager.getInstance().isUserLoggedIn(this));

        if (LocalCommManager.LoginManager.getInstance().isUserLoggedIn(this)){
            Log.e("DEBUG", "logged user found, onLoginUser called");
            ServerCommManager.getInstance().onLoginUser(this, LocalCommManager.LoginManager.getInstance().getLoggedUserId(this));
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (LocalCommManager.LoginManager.getInstance().isUserLoggedIn(LoginActivity.this)) {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount acc = result.getSignInAccount();
            LocalCommManager.LoginManager.getInstance().setGoogleSignInAccount(this, acc);
            ServerCommManager.getInstance().onLoginUser(this, acc.getId());
        }
        else {
            Toast.makeText(LoginActivity.this, R.string.log_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            Toast.makeText(this, R.string.could_not_connect_google, Toast.LENGTH_SHORT).show();
        }
    }

    /** Cycles between VISIBILITES of Google SignButton and Welcome Text
     *
     * @param loggedIn if passed <code>true</code> shows the Welcome Text, else shows the SignButton
     */
    private void switchBoxes(boolean loggedIn){
        if (loggedIn){
            userWelcomeText.setText(getString(R.string.welcome_back) + LocalCommManager.LoginManager.getInstance().getLoggedUsername(this));
            userWelcomeBox.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.INVISIBLE);
        }
        else {
            signIn.setVisibility(View.VISIBLE);
            userWelcomeBox.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public void onConnected(Bundle bundle) {
        LocalCommManager.LoginManager.getInstance().setGoogleApiClient(mGoogleApiClient);
        Log.e("service", mGoogleApiClient.isConnected() ? "api connected in login" : "api not connected in login");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("service", mGoogleApiClient.isConnected() ? "api connected in login" : "api not connected in login");
    }



    @Override
    public void handleLoginResponse(int statusCode, boolean isBusiness, ArrayList<ShortPlace> placesOwned) {
        if (statusCode == Constants.STATUS_CODE_NOT_FOUND){
            userWelcomeText.setText("Welcome " + LocalCommManager.LoginManager.getInstance().getLoggedUsername(this));
            LocalCommManager.LoginManager.getInstance().setLoggedUserTypeBusiness(this, false);
        }
        else {
            LocalCommManager.LoginManager.getInstance().setLoggedUserTypeBusiness(this, isBusiness);
            if (isBusiness){
                LocalCommManager.LoginManager.getInstance().setBusinessUserOwnedPlaces(placesOwned);
            }
        }
        switchBoxes(true);
    }

}
