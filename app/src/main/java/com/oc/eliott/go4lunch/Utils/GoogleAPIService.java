package com.oc.eliott.go4lunch.Utils;

import android.media.Image;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Photo;
import com.oc.eliott.go4lunch.Model.GooglePlaces.ResultGooglePlaces;
import com.oc.eliott.go4lunch.Model.PlaceDetails.ResultPlaceDetails;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleAPIService {
    @GET("nearbysearch/json?")
    Call<ResultGooglePlaces> getRestaurantNearby(@Query("key")String apiKey,
                                                 @Query("location")String gpsCoordinates,
                                                 @Query("rankby")String rankBy,
                                                 @Query("types")String types);

    @GET("details/json?")
    Call<ResultPlaceDetails> getRestaurantDetails(@Query("key")String apiKey,
                                                      @Query("placeid")String placeId,
                                                      @Query("fields") String fields);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
