package com.example.daniel.bitbite

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v4.content.ContextCompat.startActivity
import com.beust.klaxon.Klaxon
import kotlinx.android.parcel.Parcelize
import java.net.URL

// Calls Place Details API
fun callDetailsAPI(context : Context, place : Place) : DetailsResponse? {
    return Klaxon().parse<DetailsResponse>(URL(detailsSearchUrlBuilder(context, place.placeID)).readText())
}

// Builds URL for Place Details API call
fun detailsSearchUrlBuilder(context : Context, id : String) : String {
    return "https://maps.googleapis.com/maps/api/place/details/json?" +
            "placeid=" + id +
            "&key=" + context.getString(R.string.google_api_key)
}

@Parcelize
class DetailsResponse(val result:DetailsResults, val status:String) : Parcelable

@Parcelize
class DetailsResults(val formatted_phone_number:String = "",
                     val reviews:List<Reviews>, val website:String = "") : Parcelable

@Parcelize
class Reviews(val author_name:String = "", val text:String = "",
              val rating:Int = 0) : Parcelable