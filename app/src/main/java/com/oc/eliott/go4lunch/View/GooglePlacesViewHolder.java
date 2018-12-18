package com.oc.eliott.go4lunch.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.oc.eliott.go4lunch.Model.Result;
import com.oc.eliott.go4lunch.R;

public class GooglePlacesViewHolder extends RecyclerView.ViewHolder {
    private TextView nameRestaurant, addressRestaurant, scheduleRestaurant;
    private TextView workmatesGoingToRestaurant, distanceToRestaurant;
    private ImageView icnPeople, ratingRestaurant, icnRestaurant;

    public GooglePlacesViewHolder(View itemView) {
        super(itemView);

        nameRestaurant = itemView.findViewById(R.id.recycler_view_fragment_name_restaurant);
        addressRestaurant = itemView.findViewById(R.id.recycler_view_fragment_address_restaurant);
        scheduleRestaurant = itemView.findViewById(R.id.recycler_view_fragment_schedule_restaurant);
        workmatesGoingToRestaurant = itemView.findViewById(R.id.recycler_view_fragment_workmates_going_to_restaurant);
        distanceToRestaurant = itemView.findViewById(R.id.recycler_view_fragment_distance_to_restaurant);
        icnPeople = itemView.findViewById(R.id.recycler_view_fragment_icn_people);
        ratingRestaurant = itemView.findViewById(R.id.recycler_view_fragment_rating_restaurant);
        icnRestaurant = itemView.findViewById(R.id.recycler_view_fragment_img_restaurant);
    }

    public void updateWithGooglePlaces(Result restaurant, RequestManager glide){
        nameRestaurant.setText(restaurant.getName());
        addressRestaurant.setText(restaurant.getVicinity());

        getOpeningHours(restaurant);
    }

    private void getOpeningHours(Result restaurant){
        String openingHours;
        if(restaurant.getOpeningHours() != null) {
            if(restaurant.getOpeningHours().getOpenNow()) openingHours = "Open";
            else openingHours = "Close";
        }
        else openingHours = "Impossible to get opening hours";
        scheduleRestaurant.setText(openingHours);
    }
}
