package com.oc.eliott.go4lunch.Controller.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.oc.eliott.go4lunch.Api.UserHelper;
import com.oc.eliott.go4lunch.BuildConfig;
import com.oc.eliott.go4lunch.Controller.Activities.DetailRestaurantActivity;
import com.oc.eliott.go4lunch.Model.GooglePlaces.Result;
import com.oc.eliott.go4lunch.Model.GooglePlaces.ResultGooglePlaces;
import com.oc.eliott.go4lunch.Model.User;
import com.oc.eliott.go4lunch.R;
import com.oc.eliott.go4lunch.Utils.GoogleAPICalls;
import com.oc.eliott.go4lunch.Utils.LocationSingleton;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements GoogleAPICalls.CallbacksGooglePlaces {
    private MapView mapView; // Use to add a map to the fragment
    private GoogleMap googleMap; // Use to set a google Map
    private List<String> idRestaurantEachUser; // Contain all restaurant choose by our users
    private Marker positionMarker; // Current User position marker

    public MapViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Bind the mapview
        mapView = view.findViewById(R.id.map_view_fragment_map_view);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        // Initialize the map view
        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e){
            e.printStackTrace();
        }

        // Get a googlemap for the mapview
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                // Check the permission to get the current place of the user
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                // Add a control zoom to the map
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Add a marker to the current position of the user
                positionMarker = googleMap.addMarker(new MarkerOptions()
                        .position(LocationSingleton.getInstance().getLocation())
                        .title(getString(R.string.current_place))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Set the camera to the current user place
                CameraPosition cameraPosition = new CameraPosition.Builder().target(LocationSingleton.getInstance().getLocation()).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // Get all id restaurant for each user
                UserHelper.getAllUsers().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<User> userList = queryDocumentSnapshots.toObjects(User.class);
                        idRestaurantEachUser = new ArrayList<>();
                        for(User user : userList){
                            idRestaurantEachUser.add(user.getIdRestaurant());
                        }

                        // Search all the restaurant closed to the user
                        fetchRestaurantsNearby();
                    }
                });

                // Setup the click for each marker and open the restaurant detail
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(!marker.equals(positionMarker)){
                            Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
                            intent.putExtra("idRestaurant", marker.getSnippet());
                            startActivity(intent);
                        }
                        return false;
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // Method that all the restaurant which are closed to the user
    public void fetchRestaurantsNearby(){
        GoogleAPICalls.fetchRestaurantNearby(this, BuildConfig.ApiKey, "" + LocationSingleton.getInstance().toString() , "distance", "restaurant");
    }

    // Add a marker for each restaurant get by the googleplace api
    @Override
    public void onResponseGooglePlaces(@Nullable ResultGooglePlaces resultGP) {
        if(resultGP != null){
            for(Result result : resultGP.getResults()){
                String idRestaurant = result.getPlaceId();
                if(idRestaurantEachUser.contains(idRestaurant)){
                    LatLng restaurantLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                    googleMap.addMarker(new MarkerOptions()
                            .position(restaurantLocation)
                            .title(result.getName())
                            .snippet(idRestaurant)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                else{
                    LatLng restaurantLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                    googleMap.addMarker(new MarkerOptions()
                            .position(restaurantLocation)
                            .title(result.getName())
                            .snippet(idRestaurant)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }
        }
    }

    @Override
    public void onFailureGooglePlaces() { }
}
