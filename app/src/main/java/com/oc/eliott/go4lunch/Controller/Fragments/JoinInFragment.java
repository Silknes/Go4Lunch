package com.oc.eliott.go4lunch.Controller.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.View.JoinInAdapter;

public class JoinInFragment extends Fragment{
    private RecyclerView recyclerView;
    private JoinInAdapter joinInAdapter;
    private String idRestaurant;

    public JoinInFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_in, container, false);

        recyclerView = view.findViewById(R.id.join_in_fragment_recycler_view);

        if(getArguments() != null) idRestaurant = getArguments().getString("idRestaurant");

        this.configureRecyclerView();

        return view;
    }

    private void configureRecyclerView(){
        this.joinInAdapter = new JoinInAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()),
                Glide.with(this), idRestaurant, FirebaseAuth.getInstance().getCurrentUser().getUid());
        joinInAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(joinInAdapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.joinInAdapter);
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
