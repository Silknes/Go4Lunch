package com.oc.eliott.go4lunch.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

public class JoinInViewHolder extends RecyclerView.ViewHolder {
    private ImageView userImage;
    private TextView userRestaurant;
    public LinearLayout recyclerViewContainer;
    public View view;

    public JoinInViewHolder(View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.workmates_fragment_user_image);
        userRestaurant = itemView.findViewById(R.id.workmates_fragment_user_restaurant);
        recyclerViewContainer = itemView.findViewById(R.id.recycler_view_fragment_container);
        view = itemView.findViewById(R.id.workmates_fragment_view);
    }

    // Update with data get from Firebase
    public void updateWithDatabase(User user, RequestManager glide, String idRestaurant, String uidCurrentUser){
        if(user.getIdRestaurant().equals(idRestaurant) && !uidCurrentUser.equals(user.getUid())){
            glide.load(user.getUrlPhoto()).apply(RequestOptions.circleCropTransform()).into(userImage);
            String str = userRestaurant.getResources().getString(R.string.user_joinin, user.getUsername());
            userRestaurant.setText(str);
            view.setVisibility(View.INVISIBLE);
        }
        else{
            recyclerViewContainer.setVisibility(View.INVISIBLE);
            recyclerViewContainer.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }
}
