package com.oc.eliott.go4lunch.Utils;

import android.media.Image;
import android.support.annotation.Nullable;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Photo;
import com.oc.eliott.go4lunch.Model.GooglePlaces.ResultGooglePlaces;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleAPICalls {
    public interface CallbacksGooglePlaces{
        void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP);
        void onFailureGooglePlaces();
    }

    public interface CallbacksPlaceDetails{
        void onResponsePlaceDetails(@Nullable ResultPlaceDetails resultPD, int position);
        void onFailurePlaceDetails();
    }

    public static void fetchRestaurantNearby(CallbacksGooglePlaces callbacks, String apiKey, String gpsCoordinates, String rankBy, String types){
        final WeakReference<CallbacksGooglePlaces> callbacksWeakReference = new WeakReference<>(callbacks);
        GoogleAPIService googleAPIService = GoogleAPIService.retrofit.create(GoogleAPIService.class);
        Call<ResultGooglePlaces> call = googleAPIService.getRestaurantNearby(apiKey, gpsCoordinates, rankBy, types);
        call.enqueue(new Callback<ResultGooglePlaces>(){
            @Override
            public void onResponse(Call<ResultGooglePlaces> call, Response<ResultGooglePlaces> response){
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponseGooglePlaces(response.body());
            }
            @Override
            public void onFailure(Call<ResultGooglePlaces> call, Throwable t) {
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailureGooglePlaces();
            }
        });
    }

    public static void fetchRestaurantDetails(CallbacksPlaceDetails callbacks, String apiKey, String placeId, String fields, final int position){
        final WeakReference<CallbacksPlaceDetails> callbacksWeakReference = new WeakReference<>(callbacks);
        GoogleAPIService googleAPIService = GoogleAPIService.retrofit.create(GoogleAPIService.class);
        Call<ResultPlaceDetails> call = googleAPIService.getRestaurantDetails(apiKey, placeId, fields);
        call.enqueue(new Callback<ResultPlaceDetails>(){
            @Override
            public void onResponse(Call<ResultPlaceDetails> call, Response<ResultPlaceDetails> response){
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponsePlaceDetails(response.body(), position);
            }
            @Override
            public void onFailure(Call<ResultPlaceDetails> call, Throwable t) {
                if(callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailurePlaceDetails();
            }
        });
    }
}
