package com.oc.eliott.go4lunch.Utils;

import com.oc.eliott.go4lunch.Model.ResultGooglePlaces;

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

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
