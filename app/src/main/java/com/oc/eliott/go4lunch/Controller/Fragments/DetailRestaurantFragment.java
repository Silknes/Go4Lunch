package com.oc.eliott.go4lunch.Controller.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Model.Restaurant;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

import java.util.List;

public class DetailRestaurantFragment extends BasicFragment {
    private String name, address, urlPhoto, website, phoneNumber; // These String are used to contain the data of the restaurant
    private User modelCurrentUser; // Contain the currentUser
    private List<String> like; // Contain all user uid who like the restaurant

    public DetailRestaurantFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_restaurant, container, false);

        bindView(view);

        getRestaurantData();
        setTheLunch();

        return view;
    }

    /*
    Update the view by set the name, the address and the photo of the restaurant
    We update the FAB if user going to this restaurant
    And we check if the user already like this restaurant
    */
    private void updateViewWithRestaurantDetail(){
        nameRestaurant.setText(name);
        addressRestaurant.setText(address);
        Glide.with(this).load(urlPhoto).into(imgRestaurant);

        isSelectedLunch();

        RestaurantHelper.getRestaurant(getArguments().getString("idRestaurant")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                like = documentSnapshot.toObject(Restaurant.class).getLike();
                if(like.contains(getArguments().getString("uid"))) {
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                    likeTxt.setTextColor(getResources().getColor(R.color.like));
                }
                else {
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_orange_24dp));
                    likeTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    // Method used to allow the user to call the restaurant
    private void callTheRestaurant(){
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        });
    }

    // Method that update the like button considering if the user are liking the restaurant or not
    private void updateLike(){
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantHelper.getRestaurant(getArguments().getString("idRestaurant")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        like = documentSnapshot.toObject(Restaurant.class).getLike();
                        if(like.contains(getArguments().getString("uid"))) {
                            btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_orange_24dp));
                            likeTxt.setTextColor(getResources().getColor(R.color.colorPrimary));

                            like.remove(getArguments().getString("uid"));
                            RestaurantHelper.updateLike(getArguments().getString("idRestaurant"), like);
                        }
                        else {
                            btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                            likeTxt.setTextColor(getResources().getColor(R.color.like));

                            like.add(getArguments().getString("uid"));
                            RestaurantHelper.updateLike(getArguments().getString("idRestaurant"), like);
                        }
                    }
                });
            }
        });
    }

    // Method used to allow user to fetch the restaurant's website
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

    // When the user click on the FAB we save his choice to go or leave this restaurant and update the view
    private void setTheLunch(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLunch();
            }
        });
    }

    // Method that check if the user choose this restaurant or not
    private void isSelectedLunch(){
        UserHelper.getUser(getArguments().getString("uid")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
                if(modelCurrentUser.getIdRestaurant().equals(getArguments().getString("idRestaurant")))
                    fab.setImageResource(R.drawable.ic_check_green_24dp);
                else fab.setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
            }
        });
    }

    // Method that update the FAB considering the choice of the user to go or not in this restaurant
    private void updateLunch(){
        UserHelper.getUser(getArguments().getString("uid")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
                if(modelCurrentUser.getIdRestaurant().equals(getArguments().getString("idRestaurant"))){
                    fab.setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
                    UserHelper.updateRestaurantId(getArguments().getString("uid"), "");
                }
                else {
                    fab.setImageResource(R.drawable.ic_check_green_24dp);
                    UserHelper.updateRestaurantId(getArguments().getString("uid"), getArguments().getString("idRestaurant"));
                }
            }
        });
    }

    // Get the different data of the restarant in the DB
    private void getRestaurantData(){
        RestaurantHelper.getRestaurant(getArguments().getString("idRestaurant")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                if(restaurant != null){
                    name = restaurant.getName();
                    address = restaurant.getAddress();
                    urlPhoto = restaurant.getUrlPhoto();
                    website = restaurant.getWebsite();
                    phoneNumber = restaurant.getPhoneNumber();
                }

                // We call our diffenret method after we get back the data from the DB
                updateViewWithRestaurantDetail();
                callTheRestaurant();
                updateLike();
                fetchRestaurantWebsite();
            }
        });
    }
}
