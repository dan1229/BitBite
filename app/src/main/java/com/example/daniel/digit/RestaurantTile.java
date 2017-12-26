package com.example.daniel.digit;

import android.media.Image;

import java.net.URL;

/**
 * Created by Daniel on 12/25/2017.
 */

public class RestaurantTile {

    //Constructor
    public RestaurantTile(String name){
        this.name = name;
    }

    //Data Members
    public String name;
    public String description;
    public Image photo;
    public URL googleURL;
    public int price;
    public int rating;
    public float distance;
    private double longitude;
    private double latitude;

    //Public Methods
    public double get_long(){
        return longitude;
    }

    public double get_lat(){
        return latitude;
    }

    public void set_description(String n){
        description = n;
    }

    public void set_photo(Image n){
        photo = n;
    }

    public void set_url(URL n){
        googleURL = n;
    }

    public void set_price(int n){
        price = n;
    }

    public void set_rating(int n){
        rating = n;
    }

    public void set_dist(float n){
        distance = n;
    }

    public void set_long(double n){
        longitude = n;
    }

    public void set_lat(double n) {
        latitude = n;
    }
}
