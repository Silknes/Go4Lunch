package com.oc.eliott.go4lunch.Controller.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private String name, address, urlPhoto, website, phoneNumber;
    private User modelCurrentUser;
    private List<String> like;

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

    private void updateLike(){
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RestaurantHelper.getRestaurant(getArguments().getString("idRestaurant")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        like = documentSnapshot.toObject(Restaurant.class).getLike();
                        if(like.contains(getArguments().getString("uid"))) {

                            Snackbar.make(getView().findViewById(R.id.basic_fragment_coordinator_layout), "You unlike it ...", Snackbar.LENGTH_LONG).show();
                            btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_orange_24dp));
                            likeTxt.setTextColor(getResources().getColor(R.color.colorPrimary));

                            like.remove(getArguments().getString("uid"));
                            RestaurantHelper.updateLike(getArguments().getString("idRestaurant"), like);
                        }
                        else {
                            Snackbar.make(getView().findViewById(R.id.basic_fragment_coordinator_layout), "You like it !", Snackbar.LENGTH_LONG).show();
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

    private void setTheLunch(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLunch();
            }
        });
    }

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

    private void getRestaurantData(){
        RestaurantHelper.getRestaurant(getArguments().getString("idRestaurant")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                name = restaurant.getName();
                address = restaurant.getAddress();
                urlPhoto = restaurant.getUrlPhoto();
                website = restaurant.getWebsite();
                phoneNumber = restaurant.getPhoneNumber();

                updateViewWithRestaurantDetail();
                callTheRestaurant();
                updateLike();
                fetchRestaurantWebsite();
            }
        });
    }
}
