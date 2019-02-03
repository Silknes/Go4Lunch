package com.oc.eliott.go4lunch.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oc.eliott.go4lunch.Api.RestaurantHelper;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.Controller.Activities.MainActivity;
import com.oc.eliott.go4lunch.Model.Restaurant;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsService extends FirebaseMessagingService {
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "GO4LUNCH";
    private String currentUserRestaurantId, restaurantName;
    private List<String> sameRestaurantUsername;
    private SharedPreferences sharedPreferences;

    /*
    *  On massage received we check if current user has selected a lunch
    *  In this case we allow this user to receive the notification
    *  More over we fetch all user in our db who selected the same lunch
    */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        if(remoteMessage.getNotification() != null && getSwitchState()){
            final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                UserHelper.getUser(currentUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUserRestaurantId = documentSnapshot.toObject(User.class).getIdRestaurant();
                        if(!currentUserRestaurantId.equals("")) {
                            // Get the name of the restaurant selected by the current user
                            RestaurantHelper.getRestaurant(documentSnapshot.toObject(User.class).getIdRestaurant()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    restaurantName = documentSnapshot.toObject(Restaurant.class).getName();
                                    // Get all the user and add them to a list which is used to create the message of the notification
                                    UserHelper.getUsersCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<User> users = queryDocumentSnapshots.toObjects(User.class);
                                            sameRestaurantUsername = new ArrayList<>();
                                            for(User user : users){
                                                if(!user.getUid().equals(currentUserUid) && user.getIdRestaurant().equals(currentUserRestaurantId)){
                                                    sameRestaurantUsername.add(user.getUsername());
                                                }
                                            }
                                            sendVisualNotification(restaurantName, sameRestaurantUsername);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    // Use to create the message of the notification
    private String buildNotificationMessage(String restaurantName, List<String> sameRestaurantUsername){
        String notificationMessage = getString(R.string.content_notification, restaurantName);
        for(String str : sameRestaurantUsername){
            notificationMessage = notificationMessage + str + ", ";
        }
        if(notificationMessage.endsWith(", ")) notificationMessage = notificationMessage.substring(0, notificationMessage.length() - 2);
        return notificationMessage;
    }

    // Use to create a visual notification
    private void sendVisualNotification(String restaurantName, List<String> sameRestaurantUsername){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.content_title_notification));
        inboxStyle.addLine(buildNotificationMessage(restaurantName, sameRestaurantUsername));

        String channelId = "001";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_restaurant_menu_black_24dp)
                .setContentTitle("")
                .setContentText("")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    private boolean getSwitchState(){
        return sharedPreferences.getBoolean("switchState", false);
    }
}
