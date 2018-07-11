package com.example.daniel.bitbite

import android.content.Context
import android.content.res.Resources
import android.preference.PreferenceManager
import android.provider.Settings.Global.getString
import android.util.Log
import com.beust.klaxon.Klaxon
import java.net.URL
import java.util.ArrayList

/**====================================================================================================**/
/** Place Search API **/

/** Settings Variables **/
var OPENNOW = true
var RADIUS = 15 / 0.00062137
var RANKBY = "distance"

/**====================================================================================================**/
/** JSON Object Classes **/


class Response(val results:List<Results>, val status:String, val next_page_token:String = "")

class Results(val geometry:Geometry, val name:String="Not Available", val photos:List<Photos>? = null,
              val place_id:String="", val price_level:Int=0, val rating:Double=0.0,
              val opening_hours:Times, val types:Array<String>)

class Geometry(val location:LocationObj)

class LocationObj(val lat:Double, val lng:Double)

class Photos(val photo_reference:String="DEFAULT")

class Times(val open_now:Boolean = true)

/**====================================================================================================**/
/** Place Search API Call Functions **/


// callPlacesApi()
// Calls proper API call based on search
fun callPlacesApi(context:Context, user:MainActivity.User? = null, token:String = "" )
        : Pair<ArrayList<Place>, String>{

    return when(token){
        "" -> streamJson(placeSearchUrlBuilder(context, user!!))
        else -> streamJson(nextPageUrlBuilder(context, token))
    }
}

// streamJson()
// Gets and parses JSON detailsResponse from Places API
fun streamJson(url : String) : Pair<ArrayList<Place>, String> {

    val arrayList = ArrayList<Place>()
    Log.d("STREAM", url)

    val response = Klaxon().parse<Response>(URL(url).readText())
    if(response!!.status != "OK"){ // Response invalid
        if(response.status == "ZERO_RESULTS") { // No result
            throw RuntimeException("No result. Please try again.")
        }
        else { // Other issue
            throw RuntimeException("Technical error. Please try again.")
        }
    }

    for(i in 0 until (response.results.size)){
        val place = convertToPlace(response.results[i])
        arrayList.add(place)
    }

    return Pair(arrayList, response.next_page_token)
}

// convertToPlace()
// Convert Response object to Place object
private
fun convertToPlace(results : Results) :  Place {
    val photoRef = if (results.photos != null) results.photos[0].photo_reference else "DEFAULT"
    val name = results.name
    val placeID = results.place_id
    val description = results.types[0]
    val price = results.price_level
    val rating = results.rating.toInt()
    val openNow = results.opening_hours.open_now
    val location = DoubleArray(2)
    location[0] = results.geometry.location.lat
    location[1] = results.geometry.location.lng
    return Place(name, placeID, description, photoRef, price, rating, openNow, location)
}

/**====================================================================================================**/
/** URL Builders **/

// placeSearchUrlBuilder()
// Builds URL for PlaceSearch API Call
private
fun placeSearchUrlBuilder(context : Context, user : MainActivity.User) : String {
    // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
    // @Param
    // location = lat + lng
    // type = restaurant
    // *radius = dist. in m
    // *oppenow = true or false
    // *rankby = dist. or prom.
    // style = style spinner
    // price = price spinner
    // key = API key
    // * - choose in settings
    updateSettings(context)

    var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=${user.lat},${user.lng}" +
            "&type=restaurant" +
            "&maxprice=${user.price}"

    if(OPENNOW.toString() == "true"){ // Open Now
        url += "&opennow=$OPENNOW"
    }

    if(RANKBY == "distance") { // Rank by distance
        url += "&rankby=$RANKBY"
    } else{ // Rank by prominence (use radius)
        url += "&radius=$RADIUS"
    }

    if(user.style != "Random"){ // Add style(s)
        url += "&keyword=${user.style}"
    } else {
        //url += "&keyword=food"
    }

    url += "&key=${context.getString(R.string.google_api_key)}"

    return url
}


// nextPageUrlBuilder()
// Builds URL for PlaceSearch API Call
private
fun nextPageUrlBuilder(context : Context, token : String) : String {
    // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
    // @Param
    // pagetoken = next_page_token
    // key = API key

    return "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?key=${context.getString(R.string.google_api_key)}" +
            "&pagetoken=$token"
}

/**====================================================================================================**/
/** Settings Functions **/

// updateSettings()
// Updates settings variables
fun updateSettings(context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    RADIUS = (prefs.getInt("radius", 15) / 0.00062137)
    OPENNOW = prefs.getBoolean("opennow", true)
    RANKBY = prefs.getString("sortby", "distance")
}