package com.oc.eliott.go4lunch.Controller.Fragments;


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
import com.oc.eliott.go4lunch.BuildConfig;
import com.oc.eliott.go4lunch.Model.Result;
import com.oc.eliott.go4lunch.Model.ResultGooglePlaces;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.GoogleAPICalls;
import com.oc.eliott.go4lunch.View.GooglePlacesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment implements GoogleAPICalls.CallbacksGooglePlaces{
    private GooglePlacesAdapter adapter;
    private RecyclerView recyclerView;
    private List<Result> listRestaurant;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_fragment_recycler_view);

        this.configureRecyclerView();
        this.fetchRestaurants();

        return view;
    }

    private void configureRecyclerView(){
        this.listRestaurant = new ArrayList<>();
        this.adapter = new GooglePlacesAdapter(listRestaurant, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void fetchRestaurants(){
        GoogleAPICalls.fetchRestaurantNearby(this, BuildConfig.ApiKey, "50.6570466,3.138636099999985", "distance", "restaurant");
    }

    @Override
    public void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP) {
        if(resultGP != null) updateUI(resultGP.getResults());
    }

    @Override
    public void onFailureGooglePlaces() {
        Toast.makeText(getContext(), "Echec du téléchargement", Toast.LENGTH_LONG).show();
    }

    private void updateUI(List<Result> restaurants){
        listRestaurant.addAll(restaurants);
        adapter.notifyDataSetChanged();
    }
}
