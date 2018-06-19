package com.example.daniel.digit

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Daniel on 6/13/2018.
 */

@Parcelize
class Place (var name:String) : Parcelable {

    var placeID = ""
    var description = "N/A"
    var photoRef = "DEFAULT"
    var price = 0
    var rating = 0
    var location = DoubleArray(2)
    var googleURL = ""
    var card = 0

    // Secondary Constructor
    constructor( name:String, placeID:String, description:String, photoRef:String,
                 price:Int, rating:Int, location:DoubleArray) : this(name) {
        this.placeID = placeID
        this.description = description
        this.photoRef = photoRef
        this.price = price
        this.rating = rating
        this.location = location
    }

    // Methods--------------------------------------------------------------------------------

    //***** makeGoogleMapsURL *****//
    // Creates and returns maps URL to direct users to location
    fun makeGoogleMapsURL(): String{
        // https://www.google.com/maps/search/?api=1&parameters
        // Param
        // &query = lat + lng
        // &query_place_id = placeID
        var url =  "https://www.google.com/maps/search/?api=1&" +
                "&query=" + location[0] + "," + location[1] +
                "&query_place_id=" + placeID
        return url
    }

} // END CLASS PLACE