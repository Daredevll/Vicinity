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
import com.vicinity.vicinity.utilities.DummyModelClass;
import com.vicinity.vicinity.utilities.ServerCommManager;

import org.json.JSONObject;


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

        switchBoxes(DummyModelClass.LoginManager.getInstance().isUserLoggedIn(this));

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (DummyModelClass.LoginManager.getInstance().isUserLoggedIn(LoginActivity.this)) {
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
            DummyModelClass.LoginManager.getInstance().setGoogleSignInAccount(this, acc);
            ServerCommManager.getInstance().onLoginUser(this, acc.getId());
        }
        else {
            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            Toast.makeText(this, "Could not connect to Google", Toast.LENGTH_SHORT).show();
        }
    }

    /** Cycles between VISIBILITES of Google SignButton and Welcome Text
     *
     * @param loggedIn if passed <code>true</code> shows the Welcome Text, else shows the SignButton
     */
    private void switchBoxes(boolean loggedIn){
        if (loggedIn){
            userWelcomeText.setText("Welcome back " + DummyModelClass.LoginManager.getInstance().getLoggedUsername(this));
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
        DummyModelClass.LoginManager.getInstance().setGoogleApiClient(mGoogleApiClient);
        Log.e("service", mGoogleApiClient.isConnected() ? "api connected in login" : "api not connected in login");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("service", mGoogleApiClient.isConnected() ? "api connected in login" : "api not connected in login");
    }


    @Override
    public void toastMsg(String msg) {
        // TODO: Check if implementation here needed
    }

    @Override
    public void handleLoginResponse(int statusCode, boolean isBusiness) {
        if (statusCode == Constants.STATUS_CODE_NOT_FOUND){
            userWelcomeText.setText("Welcome " + DummyModelClass.LoginManager.getInstance().getLoggedUsername(this));
            DummyModelClass.LoginManager.getInstance().setLoggedUserTypeBusiness(this, false);
        }
        else {
            DummyModelClass.LoginManager.getInstance().setLoggedUserTypeBusiness(this, isBusiness);
        }
        switchBoxes(true);
    }

    @Override
    public void receiveReservationRequest(JSONObject reservation) {

    }

    @Override
    public void receiveReservationAnswer(JSONObject answer) {

    }
}
