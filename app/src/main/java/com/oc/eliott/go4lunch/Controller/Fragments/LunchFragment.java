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

/*
This Fragment is the same that the DetailRestaurantFragment, it only doesn't implement the FAB
Moreover we don't base our search in DB considering the Restaurant but considering the current User
 */
public class LunchFragment extends BasicFragment{
    private String name, address, urlPhoto, website, phoneNumber; // These String are used to contain the data of the restaurant
    private List<String> like; // Contain all user uid who like the restaurant
    private String idRestaurant; // Contain the restaurant uid

    public LunchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lunch, container, false);

        bindView(view);

        getIdRestaurant();
        // Put the FAB visibility to invisible
        fab.setVisibility(View.INVISIBLE);

        likeTheRestaurant();

        return view;
    }

    /*
    Update the view by set the name, the address and the photo of the restaurant
    We update the FAB if user going to this restaurant
    And we check if the user already like this restaurant
    */
    private void updateViewWithRestaurantDetail(String name, String address, String urlPhoto){
        if(getActivity() != null){
            nameRestaurant.setText(name);
            addressRestaurant.setText(address);
            Glide.with(this).load(urlPhoto).into(imgRestaurant);

            RestaurantHelper.getRestaurant(idRestaurant).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
    private void likeTheRestaurant(){
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

    // Method that update the like button considering if the user are liking the restaurant or not
    private void updateLike(){
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantHelper.getRestaurant(idRestaurant).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        like = documentSnapshot.toObject(Restaurant.class).getLike();
                        if(like.contains(getArguments().getString("uid"))) {
                            btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_orange_24dp));
                            likeTxt.setTextColor(getResources().getColor(R.color.colorPrimary));

                            like.remove(getArguments().getString("uid"));
                            RestaurantHelper.updateLike(idRestaurant, like);
                        }
                        else {
                            btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                            likeTxt.setTextColor(getResources().getColor(R.color.like));

                            like.add(getArguments().getString("uid"));
                            RestaurantHelper.updateLike(idRestaurant, like);
                        }
                    }
                });
            }
        });
    }

    // Get Current user from firestore to know where he going and then get back the data of the restaurant
    private void getUserFromFirestore(){
        UserHelper.getUser(getArguments().getString("uid")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantHelper.getRestaurant(documentSnapshot.toObject(User.class).getIdRestaurant()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                        updateViewWithRestaurantDetail(name, address, urlPhoto);
                        callTheRestaurant();
                        updateLike();
                        fetchRestaurantWebsite();
                    }
                });
            }
        });
    }

    // Method that get the restaurant id
    private void getIdRestaurant(){
        UserHelper.getUser(getArguments().getString("uid")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                idRestaurant = documentSnapshot.toObject(User.class).getIdRestaurant();

                getUserFromFirestore();
            }
        });
    }
}
