package com.oc.eliott.go4lunch.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.PlaceDetails.DataPlaceDetails;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.R;

import java.util.List;

public class GooglePlacesAdapter extends RecyclerView.Adapter<GooglePlacesViewHolder> {
    private List<Result> listRestaurants;
    private List<ResultPlaceDetails> listRestaurantDetails;
    private RequestManager glide;

    public GooglePlacesAdapter(List<Result> listRestaurants, List<ResultPlaceDetails> restaurantDetails, RequestManager glide){
        this.listRestaurants = listRestaurants;
        this.listRestaurantDetails = restaurantDetails;
        this.glide = glide;
    }

    @NonNull
    @Override
    public GooglePlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_fragment_item, parent, false);
        return new GooglePlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GooglePlacesViewHolder holder, int position) {
        holder.updateWithGooglePlaces(this.listRestaurants.get(position), this.listRestaurantDetails.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        if(this.listRestaurants.size() < 10) return this.listRestaurants.size();
        else return 10;
    }
}
