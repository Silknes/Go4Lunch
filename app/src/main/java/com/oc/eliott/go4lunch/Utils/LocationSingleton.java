package com.oc.eliott.go4lunch.Utils;

import com.google.android.gms.maps.model.LatLng;

public class LocationSingleton {
    private static final LocationSingleton ourInstance = new LocationSingleton();

    public static LocationSingleton getInstance() {
        return ourInstance;
    }

    private LocationSingleton() {
    }

    private double lat =0;
    private double lng =0;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LatLng getLocation(){
        return new LatLng(lat, lng);
    }

    public String toString(){
        String location;
        return location = lat + "," + lng;
    }
}
