package com.example.daniel.bitbite

import android.animation.ObjectAnimator
import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.toast


/**
 * Made by Daniel Nazarian
 * 7/5/2018 @ 1:00
 */

/**====================================================================================================**/
/** Updator Methods **/

// updatePhoto()
// Updates photo
fun updatePhoto(context: Context, place: Place, view: ImageView) {
    place.placePhotoCall(context, view)
}


// updateClock()
// Updates clock section
fun updateClock(context: Context, view: TextView, bool: Boolean) {
    if(bool){
        view.text = context.getString(R.string.open)
        view.setTextColor(ContextCompat.getColor(context, R.color.green))
    } else {
        view.text = context.getString(R.string.closed)
        view.setTextColor(ContextCompat.getColor(context, R.color.red))
    }
}


// updateOpennow()
// Updates open now section
fun updateOpennow(context: Context, view: TextView, bool: Boolean) {
    if(bool){
        view.text = context.getString(R.string.yes)
        view.setTextColor(ContextCompat.getColor(context, R.color.green))
    } else {
        view.text = context.getString(R.string.no)
        view.setTextColor(ContextCompat.getColor(context, R.color.red))
    }
}

// updateDistance()
// Updates distance section
fun updateDistance(view: TextView, distance: String) {
    if(distance != "")
        view.text = distance
}

// updateDuration()
// Updates duration section
fun updateDuration(view: TextView, duration: String) {
    if(duration != "")
        view.text = duration
}

// updateWebsite()
// Updates website section
fun updateWebsite(view : TextView, input : String) {
    if(!input.equals(""))
        view.text = input
}

// updatePhone()
// Update phone section
fun updatePhone(view : TextView, input : String) {
    if(!input.equals(""))
        view.text = input
}

// updateAddress()
// Update Address section
fun updateAddress(view : TextView, input : String) {
    if(input != "")
        view.text = input
}


// updateFavoritesSection()
// Update Favorites section
fun updateFavoritesSection(context: Context, txt: TextView, icon: ImageView, favorites: Boolean) {
    if(!favorites) { // Not in favorites
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorites_icon))
        txt.text = context.getString(R.string.default_favorites)
        txt.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
    } else { // Already in favorites
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorites_filled_icon))
        txt.text = context.getString(R.string.default_already_favorited)
        txt.setTextColor(ContextCompat.getColor(context, R.color.gold))
    }
}


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


// updateFavoritesList()
// Updates favorites list
fun updateFavoritesList(context: Context, place: Place, favorites: Boolean) {
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

// clearFavoritesList()
// Clears favorites list
fun clearFavoritesList(context: Context) {
    val list = ArrayList<Place>()
    setFavoriteList(context, list)
}

// setFavoriteList()
// Saves passed ArrayList as Favorites list
fun setFavoriteList(context: Context, list: ArrayList<Place>) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString("FAVORITES", json)
    editor.apply()
}

// getFavrotiesList()
// Returns saved Favorites list
fun getFavrotiesList(context: Context): ArrayList<Place>? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = prefs.getString("FAVORITES", null)
    val type = object : TypeToken<ArrayList<Place>>(){}.getType()
    return gson.fromJson(json, type)
}

// favoritesContains()
// Checks if Favorites list contains a particular placeID
fun favoritesContains(context: Context, id: String) : Boolean {
    val list = getFavrotiesList(context)
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
    val list = getFavrotiesList(context)
    if (list != null) {
        if (list.size >= 30) { // List too big
            context.toast("Favorites list cannot have more than 50 places.")
            return
        } else {
            list.add(place)
            setFavoriteList(context, list)
            return
        }
    } else{ // List is null -> make list
        val newList = ArrayList<Place>()
        newList.add(place)
        setFavoriteList(context, newList)
    }
}

// removeFromFavorites()
// Removes passed place from Favorites list
fun removeFromFavorites(context: Context, place: Place) {
    val list = getFavrotiesList(context)
    if(list != null) {
        for(i in 0 until list.size) {
            if(list[i].placeID == place.placeID) {
                list.removeAt(i)
                setFavoriteList(context, list)
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
/** Alert Dialogs **/

// dialogDirectionsWarning()
// Shows dialog confirming user wants to go place that is closed
fun dialogDirectionsWarning(place: Place, context: Context) {
    if(!place.openNow) {
        // Build dialog box
        val builder = android.support.v7.app.AlertDialog.Builder(context)
        builder.setTitle("@string/dialog_confirmation_title")
                .setMessage("${place.name} is closed right now, do you still want directions there?")

        // Yes button listener
        builder.setPositiveButton("Yes") { dialog, _ ->
            place.openMapsPage(context)
            dialog.dismiss()
        }

        // No button listener
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    } else{
        place.openMapsPage(context)
    }
}


// dialogConfirmClearFavorites() - SETTINGS FRAGMENT ONE
// Shows dialog confirming user wants to clear Favorites List
fun dialogConfirmClearFavorites(context: Context) {
    val list = getFavrotiesList(context)

    if(list != null) {
        // Build dialog box
        val builder = android.support.v7.app.AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.dialog_confirmation_title))
                .setMessage(context.getString(R.string.dialog_favorites_confirmation_text))

        // Yes button listener
        builder.setPositiveButton("Yes") { dialog, _ ->
            clearFavoritesList(context)
            dialog.dismiss()
            context.toast("Favorites list cleared!")
        }

        // No button listener
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
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