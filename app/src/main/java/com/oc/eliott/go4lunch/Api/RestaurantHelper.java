package com.oc.eliott.go4lunch.Api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oc.eliott.go4lunch.Model.Restaurant;

import java.util.List;

/*
This class contains some methods that return data from the the database
Here we want to get the collection restaurant and sometimes update the different field
Or just get some fields that will used in activities or fragments
*/

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurant";

    public static CollectionReference getRestaurantCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createRestaurant(String uid, String name, String urlPhoto, String address, String phoneNumber, String website){
        Restaurant restaurantToCreate = new Restaurant(uid, name, urlPhoto, address, phoneNumber, website);
        return RestaurantHelper.getRestaurantCollection().document(uid).set(restaurantToCreate);
    }

    public static Task<DocumentSnapshot> getRestaurant(String uid){
        return RestaurantHelper.getRestaurantCollection().document(uid).get();
    }

    public static Task<Void> updateName(String uid, String name){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("name", name);
    }

    public static Task<Void> updateUrlPhoto(String uid, String urlPhoto){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("urlPhoto", urlPhoto);
    }

    public static Task<Void> updateAddress(String uid, String address){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("address", address);
    }

    public static Task<Void> updatePhoneNumber(String uid, String phoneNumber){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("phoneNumber", phoneNumber);
    }

    public static Task<Void> updateWebsite(String uid, String website){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("website", website);
    }

    public static Task<Void> updateLike(String uid, List<String> like){
        return RestaurantHelper.getRestaurantCollection().document(uid).update("like", like);
    }
}
