package com.oc.eliott.go4lunch.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.PlaceDetails.DataPlaceDetails;
import com.oc.eliott.go4lunch.Model.PlaceDetails.Period;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public void updateWithGooglePlaces(Result restaurant, ResultPlaceDetails restaurantDetails, RequestManager glide){
        nameRestaurant.setText(restaurant.getName());
        addressRestaurant.setText(restaurant.getVicinity());

        if(restaurantDetails.getResult().getOpeningHours() != null) this.isRestaurantOpen(restaurantDetails);
        else scheduleRestaurant.setText("Cannot access opening hours");

        this.calculteDistance(restaurant);

        glide.load(restaurant.getPhotoUrl(400)).into(icnRestaurant);
    }

    private void isRestaurantOpen(ResultPlaceDetails restaurantDetails){
        Calendar calendar = Calendar.getInstance();
        for(Period period : restaurantDetails.getResult().getOpeningHours().getPeriods()){
            if(period.getClose() == null) scheduleRestaurant.setText("Open 24/7");
            else {
                if(period.getClose().getDay() == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                    if (getOpeningHour(period) == 1)
                        scheduleRestaurant.setText("Open until " + formatClosureHour(period));
                    if (getOpeningHour(period) == 2)
                        scheduleRestaurant.setText("Open at " + formatOpenHour(period));
                    if (getOpeningHour(period) == 3) scheduleRestaurant.setText("Close");
                }
            }
        }
    }

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

    private void test(Context context, long time) {
        SimpleDateFormat sdf;
        if (DateFormat.is24HourFormat(context))
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        else
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String hour = sdf.format(new Date(time));
    }

    private String formatClosureHour(Period period){
        String hour;
        if(Integer.parseInt(period.getClose().getTime().substring(0,2)) < 12)
            hour = period.getClose().getTime().substring(0,2) + ":" + period.getClose().getTime().substring(2,4) + "am";
        else hour = "" + (Integer.parseInt(period.getClose().getTime().substring(0,2)) - 12) + ":" + period.getClose().getTime().substring(2,4) + "pm";
        return hour;
    }

    private String formatOpenHour(Period period){
        String hour;
        if(Integer.parseInt(period.getOpen().getTime().substring(0,2)) < 12)
            hour = period.getOpen().getTime().substring(0,2) + ":" + period.getOpen().getTime().substring(2,4) + "am";
        else hour = "" + (Integer.parseInt(period.getOpen().getTime().substring(0,2)) - 12) + ":" + period.getOpen().getTime().substring(2,4) + "pm";
        return hour;
    }

    private void calculteDistance(Result restaurant){
        LatLng destinationLocation = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        double doubleDistanceToRestaurant = SphericalUtil.computeDistanceBetween(LocationSingleton.getInstance().getLocation(), destinationLocation);
        int intDistanceToRestaurant = (int)Math.round(doubleDistanceToRestaurant);
        String stringDistanceToRestaurant = "" + intDistanceToRestaurant + "m";
        this.distanceToRestaurant.setText(stringDistanceToRestaurant);
    }
}
