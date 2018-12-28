package com.oc.eliott.go4lunch.Model.PlaceDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataPlaceDetails {
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
}
