package com.oc.eliott.go4lunch.Controller.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Controller.Activities.BaseActivity;
import com.oc.eliott.go4lunch.Controller.Activities.DetailRestaurantActivity;
import com.oc.eliott.go4lunch.Controller.Activities.MainActivity;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.ItemClickSupport;
import com.oc.eliott.go4lunch.View.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.Listener{
    private RecyclerView recyclerView; // Use to customize the recycler view
    private WorkmatesAdapter workmatesAdapter; // Use to implement the recycler view

    public WorkmatesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        recyclerView = view.findViewById(R.id.workmates_fragment_recycler_view);

        this.configureRecyclerView();
        this.setItemClickedListener();

        return view;
    }

    // Method that configure our recycler view, importance to notice that is a recycler view from firestore
    private void configureRecyclerView(){
        this.workmatesAdapter = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()),
                Glide.with(this), this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        workmatesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(workmatesAdapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.workmatesAdapter);
    }

    // Configure the FirestoreRecyclerOptions that is necessary to use a recyclerview from firestore
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        Toast.makeText(getContext(), R.string.no_user, Toast.LENGTH_SHORT).show();
    }

    // Add a listener on each item of the recycler view
    private void setItemClickedListener(){
        workmatesAdapter.setOnItemClickListener(new WorkmatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                User user = documentSnapshot.toObject(User.class);
                if(!user.getIdRestaurant().equals("")){
                    Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
                    intent.putExtra("idRestaurant", user.getIdRestaurant());
                    startActivity(intent);
                }
                else Toast.makeText(getContext(), "No restaurant selected", Toast.LENGTH_LONG).show();
            }
        });
    }
}
