package com.example.daniel.bitbite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

/**
 * Made by Daniel Nazarian
 * 7/5/2018 @ 1:00
 */

/**====================================================================================================**/
/** Helper Methods **/


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

// ellipsizeText()
// Ellipsizes text
fun ellipsizeText(input : String, max : Int = 20) : String {
    val size = input.length
    var res = input

    if (size > max)
        res = input.substring(0, max - 3) + "..."

    return res
}

// openWebPage()
// Opens web page to passed URL
fun openWebPage(context : Context, string : String) {
    val uris = Uri.parse(string)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val bundle = Bundle()
    bundle.putBoolean("new_window", true)
    intents.putExtras(bundle)
    context.startActivity(intents)
}

// mapsReviewUrlBuilder()
// Builds URL to leave Google Maps Review
fun mapsReviewUrlBuilder(id : String) : String {
    return "https://search.google.com/local/writereview?placeid=$id"
}