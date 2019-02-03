package com.oc.eliott.go4lunch.Api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.oc.eliott.go4lunch.Model.User;

/*
This class contains some methods that return data from the the database
Here we want to get the collection user and sometimes update the different field
Or just get some fields that will used in activities or fragments
*/

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String urlPhoto, String username, String idRestaurant){
        User userToCreate = new User(uid, urlPhoto, username, idRestaurant);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<Void> updateUsername(String uid, String username){
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateRestaurantId(String uid, String idRestaurant){
        return UserHelper.getUsersCollection().document(uid).update("idRestaurant", idRestaurant);
    }

    public static Query getAllUsers(){
        return UserHelper.getUsersCollection().orderBy("idRestaurant", Query.Direction.DESCENDING);
    }
}
