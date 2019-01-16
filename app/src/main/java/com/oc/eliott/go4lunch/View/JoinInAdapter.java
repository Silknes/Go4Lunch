package com.oc.eliott.go4lunch.View;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class JoinInAdapter extends FirestoreRecyclerAdapter<User, JoinInViewHolder> {
    private final RequestManager glide;
    private String idRestaurant, uidCurrentUser;

    public JoinInAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, String idRestaurant, String uidCurrentUser){
        super(options);

        this.glide = glide;
        this.idRestaurant = idRestaurant;
        this.uidCurrentUser = uidCurrentUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull JoinInViewHolder holder, int position, @NonNull User model) {
        holder.updateWithDatabase(model, this.glide, idRestaurant, uidCurrentUser);
    }

    @NonNull
    @Override
    public JoinInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinInViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmates_fragment_item, parent, false));
    }
}
