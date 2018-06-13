package com.example.daniel.digit

import java.net.URL

/**
 * Created by Daniel on 6/13/2018.
 */
class Place (name: String){

    // Data Members
    var name:String
    //@Json(placeID = "place_id")
    var placeID = ""
    // results -> types -> grab first three or so
    var description = ""
    // results -> photos -> photo_reference
    var photoRef: String = ""
    // https://www.google.com/maps/search/?api=1&parameters
    // Param
    // &query = lat + lng
    // &query_place_id = placeID
    var googleURL: URL? = null
    //@Json(price = "price_level")
    var price = 0
    // results -> rating
    var rating = 0
    // results -> geometry -> location -> lat/lng
    var lng = 0.0
    var lat = 0.0

    // Initialization instructions
    init{
        this.name = name
    }

    // Public Methods--------------------------------------------------------------------------------

    //*****Accessor Methods*****//
    fun get_name(): String {
        return name
    }

    fun get_placeID(): String {
        return placeID
    }

    fun get_description(): String {
        return description
    }

    fun get_photoRef(): String {
        return photoRef
    }

    fun get_googleURL(): URL? {
        return googleURL
    }

    fun get_price(): Int {
        return price
    }

    fun get_rating(): Int {
        return rating
    }

    fun get_long(): Double {
        return lng
    }

    fun get_lat(): Double {
        return lat
    }

    //*****Mutator Methods*****//
    fun set_name(n: String) {
        name = n
    }

    fun set_placeID(n: String) {
        placeID = n
    }

    fun set_description(n: String) {
        description = n
    }

    fun set_photoRef(n: String) {
        photoRef = n
    }

    fun set_url(n: URL) {
        googleURL = n
    }

    fun set_price(n: Int) {
        price = n
    }

    fun set_rating(n: Int) {
        rating = n
    }

    fun set_lng(n: Double) {
        lng = n
    }

    fun set_lat(n: Double) {
        lat = n
    }

} // END CLASS PLACE