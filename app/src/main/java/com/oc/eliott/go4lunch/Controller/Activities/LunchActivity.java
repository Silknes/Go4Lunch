package com.oc.eliott.go4lunch.Controller.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Controller.Fragments.JoinInFragment;
import com.oc.eliott.go4lunch.Controller.Fragments.LunchFragment;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class LunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        // Get the current user and if he already selected a restaurant then we configure our fragments
        // If not we finish this activity and display a toast message
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.toObject(User.class).getIdRestaurant().equals("")){
                    configureAndShowFragments(documentSnapshot.toObject(User.class).getIdRestaurant());
                }
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_restaurant_selected), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    // Configure our fragments and set for DetailRestaurantFragment some arguments and add them to this activity
    private void configureAndShowFragments(String idRestaurant){
        // Use to instantiate a new LunchFragment
        LunchFragment lunchFragment = new LunchFragment();
        // Use to instantiate a new JoinInFragment
        JoinInFragment joinInFragment = new JoinInFragment();

        if(this.getCurrentUser() != null){
            Bundle bundle = new Bundle();
            bundle.putString("uid", this.getCurrentUser().getUid());
            bundle.putString("idRestaurant", idRestaurant);
            lunchFragment.setArguments(bundle);
            joinInFragment.setArguments(bundle);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.lunch_activity_linear_layout, lunchFragment);
        transaction.add(R.id.lunch_in_fragment_linear_layout, joinInFragment);
        transaction.commit();
    }
}
