package com.oc.eliott.go4lunch.Controller.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
    private GooglePlacesAdapter adapter; // Use to customize the recycler view
    private RecyclerView recyclerView; // Use to implement the recycler view
    private List<Result> listRestaurant; // Contain all closed restaurant
    private List<ResultPlaceDetails> restaurantDetails; // Contains all placeID of closed restaurant
    private AtomicInteger atomicInteger; // Use to know when we have to stop fetching for restaurant details
    private ProgressBar progressBar;

    public RecyclerViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        // Bind our recycler view
        recyclerView = view.findViewById(R.id.recycler_view_fragment_recycler_view);
        progressBar = view.findViewById(R.id.recycler_view_progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        // Call methods to configure the recycler view, to search closed restaurant and add an onClickListener on each item of the recycler view
        this.configureRecyclerView();
        this.fetchRestaurantsNearby();
        this.configureOnClickRecyclerView();

        return view;
    }

    // Method that configure our recycler view
    private void configureRecyclerView(){
        this.listRestaurant = new ArrayList<>();
        this.restaurantDetails = new ArrayList<>();
        this.adapter = new GooglePlacesAdapter(listRestaurant, restaurantDetails, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Method that configure the click on each item of the recycler view
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

    // Method that fetch all closed restaurant around user location
    public void fetchRestaurantsNearby(){
        GoogleAPICalls.fetchRestaurantNearby(this, BuildConfig.ApiKey, "" + LocationSingleton.getInstance().toString() , "distance", "restaurant");
    }

    // Method that fetch details on each closed restaurant around user location
    public void fetchRestaurantDetails(String placeID, int position){
        GoogleAPICalls.fetchRestaurantDetails(this, BuildConfig.ApiKey, placeID, "opening_hours,name,website,formatted_phone_number,formatted_address", position);
    }

    // When get back datas from Google API Places, for each restaurant, we call fetchRestaurantDetails to have more information about each restaurant
    @Override
    public void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP) {
        if(resultGP != null) {
            listRestaurant.addAll(resultGP.getResults());
            atomicInteger = new AtomicInteger(listRestaurant.size()-1);
            for (int i = 0; i < listRestaurant.size(); i++) {
                restaurantDetails.add(new ResultPlaceDetails());
            }
            for (int i = 0; i < listRestaurant.size(); i++) {
                fetchRestaurantDetails(listRestaurant.get(i).getPlaceId(), i);
            }
        }
    }

    @Override
    public void onFailureGooglePlaces() { }

    // Update the adapter when we get more details for each restaurant
    @Override
    public synchronized void onResponsePlaceDetails(@Nullable ResultPlaceDetails resultPD, int position) {
        if(resultPD != null) {
            restaurantDetails.set(position, resultPD);
            if(atomicInteger.getAndDecrement() < 1){
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFailurePlaceDetails() { }
}
