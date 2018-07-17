package com.example.daniel.bitbite

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.toast


/**
 * Made by Daniel Nazarian
 * 7/5/2018 @ 1:00
 */

/**====================================================================================================**/
/** Helper Methods **/

/**====================================================================================================**/
/** Loading Screen **/

// startLoading()
// Starts loading screen
fun startLoading(view : View) {
    if (view.visibility == View.GONE) { // Loading screen is gone, start loading
        view.rootView.isClickable = false
        view.visibility = View.VISIBLE
        rotate(view.findViewById(R.id.loading_image))
    }
}

// stopLoading()
// Stops loading screen
fun stopLoading(view : View) {
    if (view.visibility != View.GONE) { // Loading screen is visible, hide
        view.rootView.isClickable = true
        view.visibility = View.GONE
    }
}

// rotate()
// Rotates passed View
fun rotate(view : View) {
    val rotate = ObjectAnimator.ofFloat(view, View.ROTATION, -360f, 0f)
    rotate.duration = 2000
    rotate.interpolator = LinearInterpolator()
    rotate.repeatCount = 15
    rotate.start()
}

// rotateButton()
// Rotates passed View fast
fun rotateFast(time : Long, view : View) {
    val rotate = ObjectAnimator.ofFloat(view, View.ROTATION, -1440f, 0f)
    rotate.duration = time
    rotate.interpolator = DecelerateInterpolator()
    rotate.start()
}

/**====================================================================================================**/
/** Settings Methods **/


// updateFavorites()
// Updates favorites list
fun updateFavorites(context: Context, place: Place, favorites: Boolean) {
    val inList = favoritesContains(context, place.placeID)

    if(favorites) { // Selected to be in favorites list
        if(!inList) { // Not in list
            addToFavorites(context, place)
        }
    } else { // Not selected to be in favorites list
        if(inList) { // In list
            removeFromFavorites(context, place)
        }
    }
}

// setFavorites()
// Saves passed ArrayList as Favorites list
fun setFavorites(context: Context, list: ArrayList<Place>) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString("FAVORITES", json)
    editor.apply()
}

// getFavorites()
// Returns saved Favorites list
fun getFavorites(context: Context): ArrayList<Place>? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString("FAVORITES", null)
    val type = object : TypeToken<ArrayList<Place>>(){}.getType()
    return gson.fromJson(json, type)
}

// favoritesContains()
// Checks if Favorites list contains a particular placeID
fun favoritesContains(context: Context, id: String) : Boolean {
    val list = getFavorites(context)
    if(list != null) {
        for(i in 0..(list.size - 1)) {
            if(list[i].placeID == id) {
                return true
            }
        }
    }
    return false
}

// addToFavorites()
// Adds passed place to Favorites list
fun addToFavorites(context: Context, place: Place) {
    val list = getFavorites(context)
    if (list != null) {
        if (list.size >= 30) { // List too big
            context.toast("Favorites list cannot have more than 50 places.")
            return
        } else {
            list.add(place)
            setFavorites(context, list)
            return
        }
    } else{ // List is null -> make list
        val newList = ArrayList<Place>()
        newList.add(place)
        setFavorites(context, newList)
    }
}

// removeFromFavorites()
// Removes passed place from Favorites list
fun removeFromFavorites(context: Context, place: Place) {
    val list = getFavorites(context)
    if(list != null) {
        for(i in 0 until list.size) {
            if(list[i].placeID == place.placeID) {
                list.removeAt(i)
                setFavorites(context, list)
                return
            }
        }
    }
}

// getPriceSetting()
// Gets and returns price setting variable
fun getPriceSetting(context: Context) : Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt("default_price", 5)
}

// getRadiusSetting()
// Gets and returns radius setting variable
fun getRadiusSetting(context: Context) : String {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return (prefs.getInt("radius", 15) / 0.00062137).toString()
}

// getOpenNowSetting()
// Gets and returns open now setting variable
fun getOpenNowSetting(context: Context) : Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getBoolean("opennow", true)
}

// getRankBySetting()
// Gets and returns rank by setting variable
fun getRankBySetting(context: Context) : String {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString("sortby", "distance")
}


/**====================================================================================================**/
/** Intent Makers **/

// goToAboutUs()
// Go to www.BitBite.app
fun goToAboutUs(context : Context) {
    openWebPage(context, "https://www.BitBite.app")
}


// goToFeedback()
// Go to Google Play Reviews page
fun goToFeedback(context : Context) {
//
}

// openWebPage()
// Opens web page to passed URL
fun openWebPage(context : Context, url : String) {
    val uris = Uri.parse(url)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val bundle = Bundle()
    bundle.putBoolean("new_window", true)
    intents.putExtras(bundle)
    context.startActivity(intents)
}


/**====================================================================================================**/
/** Conversion Methods **/

// ratingConversion()
// Converts rating to drawable of stars based on value
fun ratingConversion(rating : Int) = when (rating) {
    1 -> R.drawable.star_1
    2 -> R.drawable.star_2
    3 -> R.drawable.star_3
    4 -> R.drawable.star_4
    5 -> R.drawable.star_5
    else -> R.drawable.default_star
}

// ratingConversion()
// Converts rating to drawable of stars based on value
fun reviewRatingConversion(rating : Int) = when (rating) {
    1 -> R.drawable.face_1
    2 -> R.drawable.face_2
    3 -> R.drawable.face_3
    4 -> R.drawable.face_4
    5 -> R.drawable.face_5
    else -> R.drawable.face_3
}

// priceConversion()
// Converts price to string of "$" based on value
fun priceConversion(price : Int) = when(price) {
    1 -> "$"
    2 -> "$$"
    3 -> "$$$"
    4 -> "$$$$"
    5 -> "$$$$$"
    else -> ""
}


/**====================================================================================================**/
/** URL Builders **/

// mapsReviewUrlBuilder()
// Builds URL to leave Google Maps Review
fun mapsReviewUrlBuilder(id : String) : String {
    return "https://search.google.com/local/writereview?placeid=$id"
}


/**====================================================================================================**/
/** Misc. Methods **/

// ellipsizeText()
// Ellipsizes text
fun ellipsizeText(input : String, max : Int = 20) : String {
    val size = input.length
    var res = input

    if (size > max)
        res = input.substring(0, max - 3) + "..."

    return res
}

// downloadPhoto()
// Downloads photo based on passed URL into passed ImageView
fun downloadPhoto(context : Context, view : ImageView, url : String) {
    Glide.with(context).load(url).into(view)

}