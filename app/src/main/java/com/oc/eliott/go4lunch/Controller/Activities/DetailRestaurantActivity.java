package com.oc.eliott.go4lunch.Controller.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.oc.eliott.go4lunch.Controller.Fragments.DetailRestaurantFragment;
import com.oc.eliott.go4lunch.Controller.Fragments.JoinInFragment;
import com.oc.eliott.go4lunch.R;

public class DetailRestaurantActivity extends BaseActivity {
    private DetailRestaurantFragment detailRestaurantFragment; // Use to instantiate a new DetailRestaurantFragment
    private JoinInFragment joinInFragment; // Use to instantiate a new JoinInFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        configureAndShowFragments(); // Call this method to add our fragments
    }

    // Configure our fragments and set for DetailRestaurantFragment some arguments and add them to this activity
    private void configureAndShowFragments(){
        detailRestaurantFragment = new DetailRestaurantFragment();
        joinInFragment = new JoinInFragment();
        if(getIntent().getExtras() != null && getCurrentUser() != null){
            Bundle bundle = new Bundle();
            bundle.putString("uid", this.getCurrentUser().getUid());
            bundle.putString("idRestaurant", getIntent().getExtras().get("idRestaurant").toString());
            detailRestaurantFragment.setArguments(bundle);
            joinInFragment.setArguments(bundle);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.detail_restaurant_fragment_linear_layout, detailRestaurantFragment);
        transaction.add(R.id.lunch_in_fragment_linear_layout, joinInFragment);
        transaction.commit();
    }
}
