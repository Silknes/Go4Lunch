package com.oc.eliott.go4lunch.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder> {
    private final RequestManager glide;
    private Listener callback;
    private String uidCurrentUser;
    public static OnItemClickListener listener;

    public interface Listener{
        void onDataChanged();
    }

    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, String uidCurrentUser){
        super(options);

        this.glide = glide;
        this.callback = callback;
        this.uidCurrentUser = uidCurrentUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, final int position, @NonNull User model) {
        holder.updateWithDatabase(model, this.glide, uidCurrentUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int thisPosition = position;
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            }
        });
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmates_fragment_item, parent, false));
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
