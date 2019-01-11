package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Model.Restaurant;
import com.oc.eliott.go4lunch.R;

import java.util.Arrays;

public class AuthentificationActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 123;

    private Button googleBtn, facebookBtn; // Button that allow the user to login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        // Bind the view we need
        googleBtn = findViewById(R.id.activity_auth_btn_google);
        facebookBtn = findViewById(R.id.activity_auth_btn_facebook);

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

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                this.createUserInFirestore();
                finish();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, "Authentification Annulé", Toast.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Pas de connexion internet", Toast.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Erreur inconnue", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
