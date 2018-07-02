package com.example.daniel.bitbite

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast

class LocationActivity : AppCompatActivity() {

    // Variables
    lateinit var place:Place
    lateinit var response:ResultsActivity.DetailsResponse
    var reviews = ArrayList<ResultsActivity.Reviews>(5)
    var website = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get place
        place = intent.getParcelableExtra("place")

        // Get response
        response = intent.getParcelableExtra("details_response")

        // Populate Location card
        updateLocation(response)

        // Set on click listener for Directions Button
        buttonDirections.setOnClickListener{
            place.openWebPage(this)
        }

        // Set on click listener for Reviews Layout
        layoutReviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.size > 0) {
                val intent = Intent(this@LocationActivity, ReviewActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList("review_list", reviews)
                intent.putExtra("myBundle", bundle)
                startActivity(intent)
            }
            else {
                toast("No reviews available.")
            }
        }

        // Set on click listener for Website
        layoutWebsite.setOnClickListener {
            if(website != "") { // Open website in browser
                val uris = Uri.parse(website)
                val intents = Intent(Intent.ACTION_VIEW, uris)
                val bundle = Bundle()
                bundle.putBoolean("new_window", true)
                intents.putExtras(bundle)
                this.startActivity(intents)
            }
        }

        // Set on click listener for Phone Number
        layoutPhone.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
        }
    }

    // Updates information displayed
    private
    fun updateLocation(response : ResultsActivity.DetailsResponse?) {

        // Photo and Opennow
        updatePhoto(place.photoRef)
        updateOpennow(place.openNow)
        updatePrice(place)

        // Name, price, description, rating
        findViewById<TextView>(R.id.locationName).text = ellipsizeText(place.name)
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))

        // Update website, phone and review
        updateWebsite(response!!.result.website)
        updatePhone(response.result.formatted_phone_number)
        updateReviws(response.result.reviews)
    }

    // Update photo
    private
    fun updatePhoto(photoRef : String){
        if(photoRef != "DEFAULT")
            placePhotoCall(photoRef, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))
    }

    // Update Open Now
    private
    fun updateOpennow(bool : Boolean) {
        if(bool){
            findViewById<TextView>(R.id.locationOpen).text = getString(R.string.yes)
            findViewById<TextView>(R.id.locationOpen).setTextColor(
                    ContextCompat.getColor(this, R.color.green))
        } else {
            findViewById<TextView>(R.id.locationOpen).text = getString(R.string.no)
            findViewById<TextView>(R.id.locationOpen).setTextColor(
                    ContextCompat.getColor(this, R.color.red))
        }
    }

    // Update price
    private
    fun updatePrice(place : Place) {
        val view = findViewById<TextView>(R.id.locationPrice)
        val s = "- (${place.priceConversion()})"
        view.text = s
    }

    // Updates website section
    private
    fun updateWebsite(input : String) {
        val view = findViewById<TextView>(R.id.locationWebsite)
        if(!input.equals(""))
            view.text = ellipsizeText(input)
        else
            view.text = resources.getString(R.string.default_website)
        view.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        website = input
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
        phone = input
    }

    // Update reviews
    private
    fun updateReviws(reviews : List<ResultsActivity.Reviews>) {
        val input = reviews[0]

        if(!reviews.isEmpty()) { // Reviews array is not empty
            findViewById<TextView>(R.id.locationReviews).text = input.text
            findViewById<TextView>(R.id.locationReviewAuthor).text = ellipsizeText(input.author_name)
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                    .getDrawable(this, ratingConversion(input.rating)))
            copyReviews(reviews)
        }
        else { // Reviews array empty
            findViewById<TextView>(R.id.locationReviews).text = resources.getString(R.string.default_review)
            findViewById<TextView>(R.id.locationReviewAuthor).text = resources.getString(R.string.default_review_author)
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                    .getDrawable(this, R.drawable.default_star))
        }
    }

    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<ResultsActivity.Reviews>?) {
        for(i in 0..(input!!.size - 1))
            reviews.add(input[i])
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

    // Converts rating to drawable of stars based on value
    private
    fun ratingConversion(rating : Int) = when (rating) {
        1 -> R.drawable.star_1
        2 -> R.drawable.star_2
        3 -> R.drawable.star_3
        4 -> R.drawable.star_4
        5 -> R.drawable.star_5
        else -> R.drawable.default_star
    }

    // Ellipsizes text
    private
    fun ellipsizeText(input : String) : String {
        val MAX_LENGTH = 25
        val size = input.length
        var res = input

        if (size > MAX_LENGTH)
            res = input.substring(0, MAX_LENGTH - 3) + "..."

        return res
    }
}
