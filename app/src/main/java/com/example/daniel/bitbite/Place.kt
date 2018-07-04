package com.example.daniel.bitbite

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by Daniel on 6/13/2018.
 */

@Parcelize
class Place (var name:String, var placeID:String, var description:String, var photoRef:String,
             var price:Int, var rating:Int, var openNow:Boolean, var location:DoubleArray) : Parcelable {

    @IgnoredOnParcel var googleMapsUrl : String? = null

    // =============================================================================================
    // Methods

    //***** openMapsPage *****//
    // Opens web page for Google Maps
    fun openMapsPage(mContext : Context) {
        val uris = Uri.parse(makeGoogleMapsURL())
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        mContext.startActivity(intents)
    }

    // Converts price to string of "$" based on value
    fun priceConversion() = when(this.price) {
        1 -> "$"
        2 -> "$$"
        3 -> "$$$"
        4 -> "$$$$"
        5 -> "$$$$$"
        else -> ""
    }

    // Converts rating to string of stars based on value
    fun ratingConversion() = when (this.rating) {
        1 -> R.drawable.star_1
        2 -> R.drawable.star_2
        3 -> R.drawable.star_3
        4 -> R.drawable.star_4
        5 -> R.drawable.star_5
        else -> R.drawable.default_star
    }

    // Removes underscores nad capitalizes descriptions
    fun fixDescription() : String {
        return this.description.capitalize().replace("_", " ")
    }

    //***** makeGoogleMapsURL *****//
    // Creates and returns maps URL to direct users to location
    private
    fun makeGoogleMapsURL(): String{
        // https://www.google.com/maps/search/?api=1&parameters
        // @Param
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

    // Calls Place Photo API and returns image
    fun placePhotoCall(context : Context, view : ImageView) {
        Glide.with(context).load(photoCallUrlBuilder(context, this.photoRef)).into(view)
    }

    // Builds URL for Place Photo API call
    private
    fun photoCallUrlBuilder(context : Context, ref : String) : String {
        return  "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=1000" +
                "&photoreference=" + ref +
                "&key=" + context.getString(R.string.google_api_key)
    }

} // END CLASS PLACE