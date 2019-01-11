package com.oc.eliott.go4lunch.Controller.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.oc.eliott.go4lunch.Controller.Fragments.DetailRestaurantFragment;
import com.oc.eliott.go4lunch.R;

public class DetailRestaurantActivity extends BaseActivity {
    private DetailRestaurantFragment detailRestaurantFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        configureAndShowDetailRestaurantFragment();
    }

    private void configureAndShowDetailRestaurantFragment(){
        detailRestaurantFragment = (DetailRestaurantFragment) getSupportFragmentManager().findFragmentById(R.id.lunch_activity_linear_layout);
        if(detailRestaurantFragment == null){
            detailRestaurantFragment = new DetailRestaurantFragment();
            if(getIntent().getExtras() != null && getCurrentUser() != null){
                Bundle bundle = new Bundle();
                bundle.putString("uid", this.getCurrentUser().getUid());
                bundle.putString("idRestaurant", getIntent().getExtras().get("idRestaurant").toString());
                detailRestaurantFragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction().add(R.id.detail_restaurant_activity_linear_layout, detailRestaurantFragment).commit();
        }
    }
}
