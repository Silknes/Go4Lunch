package com.oc.eliott.go4lunch.Controller.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Controller.Fragments.LunchFragment;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class LunchActivity extends BaseActivity {
    private LunchFragment lunchFragment;
    public static final String KEY_SHARED_PREFERENCES = "KEY_SHARED_PREFERENCES";
    private boolean isAtLeastOneRestaurantSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.toObject(User.class).getIdRestaurant().equals("")){
                    configureAndShowLunchFragment();
                }
                else {
                    Toast.makeText(getBaseContext(), "No restaurant selected", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void configureAndShowLunchFragment(){
        lunchFragment = (LunchFragment) getSupportFragmentManager().findFragmentById(R.id.lunch_activity_linear_layout);
        if(lunchFragment == null){
            lunchFragment = new LunchFragment();
            if(this.getCurrentUser() != null){
                Bundle bundle = new Bundle();
                bundle.putString("uid", this.getCurrentUser().getUid());
                lunchFragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction().add(R.id.lunch_activity_linear_layout, lunchFragment).commit();
        }
    }
}
