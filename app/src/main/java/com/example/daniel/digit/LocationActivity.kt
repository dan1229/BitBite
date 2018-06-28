package com.example.daniel.digit

import android.graphics.Color
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.beust.klaxon.Klaxon
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.w3c.dom.Text
import java.net.URL

class LocationActivity : AppCompatActivity() {

    // Variables
    lateinit var place:Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get place
        place = intent.getParcelableExtra<Place>("location")

        // Async call
        doAsync {
            // Call Place Details API
            var response = callDetailsAPI(place)

            uiThread {
                // Set data
                updateLocation(response)
            }
        }

        // Set on click listener for Directions Button
        buttonDirections.setOnClickListener{

        }

        // Set on click listener for Fave Button
        buttonFave.setOnClickListener{

        }

        // Set on click listener for Reviews Layout
        reviewsLayout.setOnClickListener{

        }
    }

    // Updates information displayed
    private
    fun updateLocation(response : DetailsResponse?) {

        // Place info updates
        if(place.photoRef != "DEFAULT")
            placePhotoCall(place.photoRef, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))

        // Open Now update
        if(place.openNow){
            findViewById<TextView>(R.id.locationOpen).text = "Yes"
            findViewById<TextView>(R.id.locationOpen).setTextColor(Color.GREEN)
        } else {
            findViewById<TextView>(R.id.locationOpen).text = "No"
            findViewById<TextView>(R.id.locationOpen).setTextColor(Color.RED)
        }

        // Name, price, description, rating
        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationPrice).text = place.priceConversion()
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))


        updateWebsite(response!!.results.website)
        updatePhone(response.results.formatted_phone_number)
        updateReview(response.results.reviews)
    }

    // Updates website section
    private
    fun updateWebsite(input : String) {
        val view = findViewById<TextView>(R.id.locationWebsite)
        if(!input.equals(""))
            view.text = input
        else
            view.text = resources.getString(R.string.default_website)
        view.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    // Updates phone section
    private
    fun updatePhone(input : String) {
        val view = findViewById<TextView>(R.id.locationPhone)
        if(!input.equals(""))
            view.text = input
        else
            view.text = resources.getString(R.string.default_phone)
        view.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    // Updates review section
    private
    fun updateReview(input : List<Reviews>){
        // Update reviews
        if(!input.isEmpty()) { // Reviews array is not empty
            findViewById<TextView>(R.id.locationReviews).text = input[0].text
            findViewById<TextView>(R.id.locationReviewAuthor).text = input[0].author_name
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                    .getDrawable(this, ratingConversion(input[0].rating)))
        }
        else { // Reviews array empty
            findViewById<TextView>(R.id.locationReviews).text = resources.getString(R.string.default_review)
            findViewById<TextView>(R.id.locationReviewAuthor).text = resources.getString(R.string.default_review_author)
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                    .getDrawable(this, R.drawable.default_star))
        }
    }

    // Calls Place Photo API and returns image
    private
    fun placePhotoCall(ref : String, view : ImageView) {
        Glide.with(this).load(createPhotosRequestURL(ref)).into(view)
    }

    // Creates URL for Place Photo API request
    private
    fun createPhotosRequestURL(ref : String) : String {
        return  "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=1000" +
                "&photoreference=" + ref +
                "&key=" + getString(R.string.google_api_key)
    }

    class DetailsResponse(val results:DetailsResults, val status:String)

    class DetailsResults(val formatted_phone_number:String = "",
                         val reviews:List<Reviews>, val website:String = "")

    class Reviews(val author_name:String = "", val text:String = "", val rating:Int)

    // Builds URL for Place Details API call
    private
    fun detailsSearchUrlBuilder() : String {
        return "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + place.placeID +
                "&key=" + getString(R.string.google_api_key)
    }

    // Calls Place Details API
    private
    fun callDetailsAPI(place : Place) : DetailsResponse? {
        var response = Klaxon().parse<DetailsResponse>(URL(detailsSearchUrlBuilder()).readText())
        return response
        // handle error
    }

    // Converts rating to string of stars based on value
    fun ratingConversion(rating : Int) = when (rating) {
        1 -> R.drawable.star_1
        2 -> R.drawable.star_2
        3 -> R.drawable.star_3
        4 -> R.drawable.star_4
        5 -> R.drawable.star_5
        else -> R.drawable.default_star
    }
}
