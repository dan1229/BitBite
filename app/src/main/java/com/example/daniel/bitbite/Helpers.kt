package com.example.daniel.bitbite

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