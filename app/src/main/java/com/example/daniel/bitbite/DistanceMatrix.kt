package com.example.daniel.bitbite

import android.content.Context
import android.util.Log
import com.beust.klaxon.Klaxon
import java.net.URL
import android.util.Pair



/**====================================================================================================**/
/** Distance Matrix API **/


/**====================================================================================================**/
/** JSON Object Classes **/

class DistanceResponse(val rows:List<RowsObj>, val status:String)

class RowsObj(val elements:List<Elements>)

class Elements(val distance:DistanceObj, val duration:DurationObj)

class DistanceObj(val text:String)

class DurationObj(val text:String)

/**====================================================================================================**/
/** Distance Matrix API Call Functions **/

// callPlacesApi()
// Gets and parses JSON detailsResponse from Places API
fun callDistanceApi(context : Context, olat : Double, olng : Double, id : String) : Pair<String, String> {

    val origin = Pair(olat, olng)
    val url = distanceMatrixUrlBuilder(context, origin, id)

    val response = Klaxon().parse<DistanceResponse>(URL(url).readText())
    if(response!!.status != "OK"){ // Response invalid
        return Pair("", "")
    } else {
        return Pair(response.rows[0].elements[0].distance.text,
                response.rows[0].elements[0].duration.text)
    }
}

/**====================================================================================================**/
/** URL Builders **/

// distanceMatrixUrlBuilder()
//
private
fun distanceMatrixUrlBuilder(context : Context,
                             origin : Pair<Double, Double>,
                             id : String) : String {

    return "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial" +
        "&origins=${origin.first},${origin.second}" +
        "&destinations=place_id:$id" +
        "&key=${context.getString(R.string.google_api_key)}"
}