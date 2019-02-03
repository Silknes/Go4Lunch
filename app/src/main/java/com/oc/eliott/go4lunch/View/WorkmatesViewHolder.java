package com.oc.eliott.go4lunch.View;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Model.Restaurant;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {
    private ImageView userImage;
    private TextView userRestaurant;
    public LinearLayout recyclerViewContainer;

    private final int colorNoRestaurant;

    public WorkmatesViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.workmates_fragment_user_image);
        userRestaurant = itemView.findViewById(R.id.workmates_fragment_user_restaurant);
        recyclerViewContainer = itemView.findViewById(R.id.recycler_view_fragment_container);

        colorNoRestaurant = ContextCompat.getColor(itemView.getContext(), R.color.no_restaurant);
    }

    // Update with data get from Firebase
    public void updateWithDatabase(User user, RequestManager glide, String uidCurrentUser){
        if(!uidCurrentUser.equals(user.getUid())){
            glide.load(user.getUrlPhoto()).apply(RequestOptions.circleCropTransform()).into(userImage);
            updateTextView(user);
        }
        else{
            recyclerViewContainer.setVisibility(View.INVISIBLE);
            recyclerViewContainer.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    // Update userRestaurant according to user choice
    private void updateTextView(User user){
        if(!user.getIdRestaurant().equals("")){
            getRestaurantName(user);
        }
        else{
            String str = userRestaurant.getResources().getString(R.string.user_going_nowhere, user.getUsername());
            userRestaurant.setText(str);
            userRestaurant.setTextColor(colorNoRestaurant);
        }
    }

    // Get the restaurant name from Firestore
    private void getRestaurantName(User user){
        final User myUser = user;
        RestaurantHelper.getRestaurant(user.getIdRestaurant()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameRestaurant = documentSnapshot.toObject(Restaurant.class).getName();
                String str = userRestaurant.getResources().getString(R.string.user_choose_one_restaurant, myUser.getUsername(), nameRestaurant);
                userRestaurant.setText(str);

            }
        });
    }
}
