package com.oc.eliott.go4lunch.Utils;

import android.support.annotation.Nullable;

import com.oc.eliott.go4lunch.Model.ResultGooglePlaces;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleAPICalls {
    public interface CallbacksGooglePlaces{
        void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP);
        void onFailureGooglePlaces();
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
}
