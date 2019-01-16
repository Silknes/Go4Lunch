package com.oc.eliott.go4lunch.Controller.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oc.eliott.go4lunch.R;

public class BasicFragment extends Fragment {
    protected ImageView imgRestaurant;
    protected TextView nameRestaurant, addressRestaurant, likeTxt;
    protected ImageButton btnCall, btnLike, btnWebsite;
    protected FloatingActionButton fab;

    public BasicFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic, container, false);
    }

    protected void bindView(View view){
        imgRestaurant = view.findViewById(R.id.basic_fragment_img_restaurant);
        nameRestaurant = view.findViewById(R.id.basic_fragment_name_restaurant);
        addressRestaurant = view.findViewById(R.id.basic_fragment_address_restaurant);
        likeTxt = view.findViewById(R.id.basic_fragment_like_txt);
        btnCall = view.findViewById(R.id.basic_fragment_call_btn);
        btnLike = view.findViewById(R.id.basic_fragment_like_btn);
        btnWebsite = view.findViewById(R.id.basic_fragment_website_btn);
        fab = view.findViewById(R.id.basic_fragment_fab);
    }

}
