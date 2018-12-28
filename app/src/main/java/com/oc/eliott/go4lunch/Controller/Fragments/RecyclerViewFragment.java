package com.oc.eliott.go4lunch.Controller.Fragments;


import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.oc.eliott.go4lunch.BuildConfig;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Photo;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.GooglePlaces.ResultGooglePlaces;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.GoogleAPICalls;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;
import com.oc.eliott.go4lunch.View.GooglePlacesAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment implements GoogleAPICalls.CallbacksGooglePlaces, GoogleAPICalls.CallbacksPlaceDetails{
    private GooglePlacesAdapter adapter;
    private RecyclerView recyclerView;
    private List<Result> listRestaurant;
    private List<ResultPlaceDetails> restaurantDetails;
    private List<String> listPlaceID, listPhotoReference;
    private int position = 0;

    public RecyclerViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_fragment_recycler_view);

        this.configureRecyclerView();
        this.fetchRestaurantsNearby();

        return view;
    }

    private void configureRecyclerView(){
        this.listRestaurant = new ArrayList<>();
        this.restaurantDetails = new ArrayList<>();
        this.adapter = new GooglePlacesAdapter(listRestaurant, restaurantDetails, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void fetchRestaurantsNearby(){
        GoogleAPICalls.fetchRestaurantNearby(this, BuildConfig.ApiKey, "" + LocationSingleton.getInstance().toString() , "distance", "restaurant");
    }

    public void fetchRestaurantDetails(String placeID){
        GoogleAPICalls.fetchRestaurantDetails(this, BuildConfig.ApiKey, placeID, "opening_hours");
    }

    // Ne plus utiliser listPlaceID récupérer l'ID depuis listRestaurant
    @Override
    public void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP) {
        if(resultGP != null) {
            listRestaurant.addAll(resultGP.getResults());
            listPlaceID = new ArrayList<>();
            for(Result result : resultGP.getResults()){
                listPlaceID.add(result.getPlaceId());
            }
            fetchRestaurantDetails(listPlaceID.get(0));
        }
    }

    @Override
    public void onFailureGooglePlaces() { }

    @Override
    public void onResponsePlaceDetails(@Nullable ResultPlaceDetails resultPD) {
        if(resultPD != null) {
            restaurantDetails.add(resultPD);
            position = position + 1;
            if(position < 10){
                fetchRestaurantDetails(listPlaceID.get(position));
            }
            if(position == 10){
                updateUI();
                //Toast.makeText(getContext(), listPlaceID.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailurePlaceDetails() { }

    private void updateUI(){
        adapter.notifyDataSetChanged();
    }
}
