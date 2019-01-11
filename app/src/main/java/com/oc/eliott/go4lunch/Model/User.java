package com.oc.eliott.go4lunch.Model;

public class User {
    private String uid;
    private String urlPhoto;
    private String username;
    private String idRestaurant;

    public User(){}

    public User(String uid, String urlPhoto, String username, String idRestaurant) {
        this.uid = uid;
        this.urlPhoto = urlPhoto;
        this.username = username;
        this.idRestaurant = idRestaurant;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }
}
