package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.R;

import java.util.Arrays;

public class AuthentificationActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 123; // ID used to get back data when user Signin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        // Bind the view we need
        Button googleBtn = findViewById(R.id.activity_auth_btn_google);
        Button facebookBtn = findViewById(R.id.activity_auth_btn_facebook);
        Button emailBtn = findViewById(R.id.activity_auth_btn_email);

        // Setup the click for Email Connection Button
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInActivity();
            }
        });

        // Setup the click for Google Connection Button
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInWithGoogleActivity();
            }
        });

        // Setup the click for Facebook Connection Button
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInActivityWithFacebook();
            }
        });
    }

    // Method wich configure the connection with an email
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN);
    }

    // Method wich configure the connection with Google
    private void startSignInWithGoogleActivity(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN);
    }

    // Method wich configure the connection with Facebook
    private void startSignInActivityWithFacebook(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN);
    }

    // Create a new User in Firestore
    private void createUserInFirestore(){
        if(this.getCurrentUser() != null){
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String urlPhoto = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String idRestaurant = "";

            UserHelper.createUser(uid, urlPhoto, username, idRestaurant);
        }
    }

    // Method witch close this Activity if the resultCode is OK
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponseAfterSignIn(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) if(resultCode == RESULT_OK) finish();
    }

    // Call the method which create the user in Firestore or display an error message if the authentification failed
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                this.createUserInFirestore();
                finish();
            } else {
                if (response == null) {
                    Toast.makeText(this, getResources().getString(R.string.authentification_canceled), Toast.LENGTH_LONG).show();
                } else if (response.getError() != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else if (response.getError() != null && response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
