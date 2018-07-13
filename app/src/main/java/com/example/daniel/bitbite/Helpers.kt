package com.example.daniel.bitbite

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.daniel.bitbite.R.id.loading_image
import java.net.URL

/**
 * Made by Daniel Nazarian
 * 7/5/2018 @ 1:00
 */

/**====================================================================================================**/
/** Helper Methods **/

/**====================================================================================================**/
/** Animations **/

// loadingScreen()
// Updates and animates loading screen
fun loadingScreen(view : View) {
    if (view.visibility == View.GONE) { // Loading screen is gone, start loading
        view.visibility = View.VISIBLE
        rotate(view.findViewById(R.id.loading_image))
    } else { // Loading screen is visible, hide
        view.visibility = View.GONE
    }
}

fun rotate(view : View) {
    val rotate = ObjectAnimator.ofFloat(view, View.ROTATION, -360f, 0f)
    rotate.duration = 2000
    rotate.interpolator = LinearInterpolator()
    rotate.repeatCount = 100
    rotate.start()
}

/**====================================================================================================**/
/** Settings Methods **/

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