package com.oc.eliott.go4lunch.Controller.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.BuildConfig;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;
import com.oc.eliott.go4lunch.Model.Restaurant;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.GoogleAPICalls;

/**
 * A simple {@link Fragment} subclass.
 */
public class LunchFragment extends BasicFragment{
    private String name, address, urlPhoto, website, phoneNumber;

    public LunchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lunch, container, false);

        bindView(view);

        getUserFromFirestore();
        fab.setVisibility(View.INVISIBLE);

        likeTheRestaurant();

        return view;
    }

    private void updateViewWithRestaurantDetail(String name, String address, String urlPhoto){
        if(getActivity() != null){
            nameRestaurant.setText(name);
            addressRestaurant.setText(address);
            Glide.with(this).load(urlPhoto).into(imgRestaurant);
        }
    }

    private void callTheRestaurant(){
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel", "0761256954", null));
                startActivity(intent);
            }
        });
    }

    private void likeTheRestaurant(){
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getView().findViewById(R.id.basic_fragment_coordinator_layout), "You like it !", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchRestaurantWebsite(){
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if(getArguments() != null) intent.setData(Uri.parse(website));
                startActivity(intent);
            }
        });
    }

    private void getUserFromFirestore(){
        UserHelper.getUser(getArguments().getString("uid")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantHelper.getRestaurant(documentSnapshot.toObject(User.class).getIdRestaurant()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.toObject(Restaurant.class).getName();
                        address = documentSnapshot.toObject(Restaurant.class).getAddress();
                        urlPhoto = documentSnapshot.toObject(Restaurant.class).getUrlPhoto();
                        website = documentSnapshot.toObject(Restaurant.class).getWebsite();
                        phoneNumber = documentSnapshot.toObject(Restaurant.class).getPhoneNumber();

                        updateViewWithRestaurantDetail(name, address, urlPhoto);
                        callTheRestaurant();
                        fetchRestaurantWebsite();
                    }
                });
            }
        });
    }
}
