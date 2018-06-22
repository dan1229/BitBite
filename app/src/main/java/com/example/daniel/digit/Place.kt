package com.example.daniel.digit

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Daniel on 6/13/2018.
 */

@Parcelize
class Place (var name:String, var placeID:String, var description:String, var photoRef:String,
             var price:Int, var rating:Int, var location:DoubleArray) : Parcelable {

    var googleMapsUrl : String? = null
    var image : Drawable? = null

    // =============================================================================================
    // Methods

    //***** openWebPage *****//
    // Opens web page for Google Maps
    fun openWebPage( mContext : Context) {
        val uris = Uri.parse(makeGoogleMapsURL())
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        mContext.startActivity(intents)
    }

    //***** makeGoogleMapsURL *****//
    // Creates and returns maps URL to direct users to location
    private
    fun makeGoogleMapsURL(): String{
        // https://www.google.com/maps/search/?api=1&parameters
        // Param
        // &query = lat + lng
        // &query_place_id = placeID
        var url = this.googleMapsUrl
        if(url == null) {
            url = "https://www.google.com/maps/search/?api=1&" +
                    "&query=" + location[0] + "," + location[1] +
                    "&query_place_id=" + placeID
            this.googleMapsUrl = url
        }
        return url
    }

} // END CLASS PLACE