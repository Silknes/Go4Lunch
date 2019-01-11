package com.oc.eliott.go4lunch.Controller.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.oc.eliott.go4lunch.BuildConfig;
import com.oc.eliott.go4lunch.Controller.Activities.DetailRestaurantActivity;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.GooglePlaces.ResultGooglePlaces;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.GoogleAPICalls;
import com.oc.eliott.go4lunch.Utils.ItemClickSupport;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;
import com.oc.eliott.go4lunch.View.GooglePlacesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RecyclerViewFragment extends Fragment implements GoogleAPICalls.CallbacksGooglePlaces, GoogleAPICalls.CallbacksPlaceDetails{
    private GooglePlacesAdapter adapter;
    private RecyclerView recyclerView;
    private List<Result> listRestaurant;
    private List<ResultPlaceDetails> restaurantDetails;
    private int position = 0;

    private AtomicInteger requestCount;

    public RecyclerViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_fragment_recycler_view);

        this.configureRecyclerView();
        this.fetchRestaurantsNearby();
        this.configureOnClickRecyclerView();

        return view;
    }

    private void configureRecyclerView(){
        this.listRestaurant = new ArrayList<>();
        this.restaurantDetails = new ArrayList<>();
        this.adapter = new GooglePlacesAdapter(listRestaurant, restaurantDetails, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.recycler_view_fragment_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
                        intent.putExtra("idRestaurant", listRestaurant.get(position).getPlaceId());
                        startActivity(intent);
                    }
                });
    }

    public void fetchRestaurantsNearby(){
        GoogleAPICalls.fetchRestaurantNearby(this, BuildConfig.ApiKey, "" + LocationSingleton.getInstance().toString() , "distance", "restaurant");
    }

    public void fetchRestaurantDetails(String placeID){
        GoogleAPICalls.fetchRestaurantDetails(this, BuildConfig.ApiKey, placeID, "opening_hours,name,website,formatted_phone_number,formatted_address");
    }

    @Override
    public void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP) {
        if(resultGP != null) {
            listRestaurant.addAll(resultGP.getResults());
            fetchRestaurantDetails(listRestaurant.get(0).getPlaceId());
        }
    }

    @Override
    public void onFailureGooglePlaces() { }

    @Override
    public void onResponsePlaceDetails(@Nullable ResultPlaceDetails resultPD) {
        if(resultPD != null) {
            restaurantDetails.add(resultPD);
            position = position + 1;
            if(position < 20){
                fetchRestaurantDetails(listRestaurant.get(position).getPlaceId());
            }
            if(position == 20){
                updateUI();
            }
        }
    }

    @Override
    public void onFailurePlaceDetails() { }

    private void updateUI(){
        adapter.notifyDataSetChanged();
    }
}
