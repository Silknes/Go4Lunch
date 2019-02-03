package com.oc.eliott.go4lunch.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.PlaceDetails.Period;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GooglePlacesViewHolder extends RecyclerView.ViewHolder {
    private TextView nameRestaurant, addressRestaurant, scheduleRestaurant;
    private TextView workmatesGoingToRestaurant, distanceToRestaurant;
    private ImageView icnPeople, icnRestaurant;
    private RatingBar ratingRestaurant;

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

    public void updateWithGooglePlaces(Result restaurant, ResultPlaceDetails restaurantDetails, RequestManager glide){
        nameRestaurant.setText(restaurantDetails.getResult().getName());
        addressRestaurant.setText(restaurant.getVicinity());

        if(restaurantDetails.getResult().getOpeningHours() != null) this.isRestaurantOpen(restaurantDetails);
        else scheduleRestaurant.setText(R.string.default_openinghours_text);

        this.calculteDistance(restaurant);

        glide.load(restaurant.getPhotoUrl(400)).into(icnRestaurant);

        createRestaurantInFirestore(restaurant, restaurantDetails);

        countSameRestaurantUser(restaurant);

        updateRatingBar(restaurant);
    }

    // Method that set the rating according to the rating get from GooglePlaces
    private void updateRatingBar(Result restaurant){
        ratingRestaurant.setNumStars(3);
        ratingRestaurant.setMax(6);
        int rating;
        if(restaurant.getRating() != null) {
            rating = restaurant.getRating().intValue();
            ratingRestaurant.setRating((float)(3*rating/5));
        }
    }

    // Create a restaurant in Firestore
    private void createRestaurantInFirestore(Result restaurant, ResultPlaceDetails restaurantDetails){
        String uid = restaurant.getPlaceId();
        String name = restaurant.getName();
        String address = restaurant.getVicinity();
        String urlPhoto = restaurant.getPhotoUrl(400);
        String phoneNumber = restaurantDetails.getResult().getFormattedPhoneNumber();
        String website = restaurantDetails.getResult().getWebsite();
        RestaurantHelper.createRestaurant(uid, name, urlPhoto, address, phoneNumber, website);
    }

    // Method that format the opening hours
    private void isRestaurantOpen(ResultPlaceDetails restaurantDetails){
        Calendar calendar = Calendar.getInstance();
        for(Period period : restaurantDetails.getResult().getOpeningHours().getPeriods()){
            if(period.getClose() == null) scheduleRestaurant.setText(R.string.restaurant_always_open);
            else {
                if(period.getClose().getDay() == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                    if (getOpeningHour(period) == 1){
                        String openingHours = scheduleRestaurant.getResources().getString(R.string.restaurant_open_until, formatClosureHour(period));
                        scheduleRestaurant.setText(openingHours);
                        scheduleRestaurant.setTextColor(scheduleRestaurant.getContext().getResources().getColor(R.color.ok_color));
                    }
                    if (getOpeningHour(period) == 2){
                        String openingHours = scheduleRestaurant.getResources().getString(R.string.restaurant_open_at, formatClosureHour(period));
                        scheduleRestaurant.setText(openingHours);
                    }
                    if (getOpeningHour(period) == 3) scheduleRestaurant.setText(R.string.restaurant_close);
                }
            }
        }
    }

    // Method that get opening hours from GooglePlaces
    private int getOpeningHour(Period period){
        Calendar calendar = Calendar.getInstance();
        int currentHour = Integer.parseInt("" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
        int closureHour = Integer.parseInt(period.getClose().getTime());
        int openHour = Integer.parseInt(period.getOpen().getTime());
        if(currentHour <= closureHour) {
            if(currentHour >= openHour) return 1;
            else return 2;
        }
        else return 3;
    }

    // Method that format closure hour
    private String formatClosureHour(Period period){
        String hour;
        if(Integer.parseInt(period.getClose().getTime().substring(0,2)) < 12)
            hour = period.getClose().getTime().substring(0,2) + ":" + period.getClose().getTime().substring(2,4) + "am";
        else hour = "" + (Integer.parseInt(period.getClose().getTime().substring(0,2)) - 12) + ":" + period.getClose().getTime().substring(2,4) + "pm";
        return hour;
    }

    // Method that format opening hour
    private String formatOpenHour(Period period){
        String hour;
        if(Integer.parseInt(period.getOpen().getTime().substring(0,2)) < 12)
            hour = period.getOpen().getTime().substring(0,2) + ":" + period.getOpen().getTime().substring(2,4) + "am";
        else hour = "" + (Integer.parseInt(period.getOpen().getTime().substring(0,2)) - 12) + ":" + period.getOpen().getTime().substring(2,4) + "pm";
        return hour;
    }

    // Method that calculate the distance between user location and restaurant location
    private void calculteDistance(Result restaurant){
        LatLng destinationLocation = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        double doubleDistanceToRestaurant = SphericalUtil.computeDistanceBetween(LocationSingleton.getInstance().getLocation(), destinationLocation);
        int intDistanceToRestaurant = (int)Math.round(doubleDistanceToRestaurant);
        String stringDistanceToRestaurant = "" + intDistanceToRestaurant + "m";
        this.distanceToRestaurant.setText(stringDistanceToRestaurant);
    }

    // Method that count user going to one restaurant
    private void countSameRestaurantUser(final Result restaurant){
        UserHelper.getAllUsers().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> userList = queryDocumentSnapshots.toObjects(User.class);
                List<User> sameRestaurantUserList = new ArrayList<>();
                for(User user : userList) {
                    if(user.getIdRestaurant().equals(restaurant.getPlaceId()) && !user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        sameRestaurantUserList.add(user);
                    }
                }
                workmatesGoingToRestaurant.setText("(" + sameRestaurantUserList.size() + ")");
            }
        });
    }
}
