package com.oc.eliott.go4lunch.Model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String uid;
    private String name;
    private String urlPhoto;
    private String address;
    private String phoneNumber;
    private String website;
    private List<String> like;

    public Restaurant(){}

    public Restaurant(String uid, String name, String urlPhoto, String address, String phoneNumber, String website) {
        this.uid = uid;
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.like = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getLike() {
        return like;
    }

    public void setLike(List<String> like) {
        this.like = like;
    }
}
