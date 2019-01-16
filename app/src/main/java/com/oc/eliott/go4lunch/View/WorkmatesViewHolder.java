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

    private void updateTextView(User user){
        if(!user.getIdRestaurant().equals("")){
            getRestaurantName(user);
        }
        else{
            userRestaurant.setText(user.getUsername() + " hasn't decided yet");
            userRestaurant.setTextColor(colorNoRestaurant);
        }
    }

    private void getRestaurantName(User user){
        final User myUser = user;
        RestaurantHelper.getRestaurant(user.getIdRestaurant()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nameRestaurant = documentSnapshot.toObject(Restaurant.class).getName();
                userRestaurant.setText(myUser.getUsername() + " will eat at \"" + nameRestaurant + "\"");

            }
        });
    }
}
