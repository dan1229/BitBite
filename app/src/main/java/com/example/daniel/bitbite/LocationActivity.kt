package com.example.daniel.bitbite

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.beust.klaxon.Klaxon
import com.bumptech.glide.Glide
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.net.URL

class LocationActivity : AppCompatActivity() {

    // Variables
    lateinit var place:Place
    var reviews = ArrayList<Reviews>(5)
    var website = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get place
        place = intent.getParcelableExtra("location")

        // Async call
        doAsync {
            // Call Place Details API
            var response = callDetailsAPI()

            uiThread {
                // Populate card
                updateLocation(response)
            }
        }

        // Set on click listener for Directions Button
        buttonDirections.setOnClickListener{
            place.openWebPage(this)
        }

        // Set on click listener for Reviews Layout
        layoutReviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.size != 0) {
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
            if(phone != "") // Opens dialer with phone number
                makeCall(phone)
        }
    }

    // Updates information displayed
    private
    fun updateLocation(response : DetailsResponse?) {

        // Place photo update
        if(place.photoRef != "DEFAULT")
            placePhotoCall(place.photoRef, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))

        // Open Now update
        if(place.openNow){
            findViewById<TextView>(R.id.locationOpen).text = getString(R.string.yes)
            findViewById<TextView>(R.id.locationOpen).setTextColor(Color.GREEN)
        } else {
            findViewById<TextView>(R.id.locationOpen).text = getString(R.string.no)
            findViewById<TextView>(R.id.locationOpen).setTextColor(Color.RED)
        }

        // Name, price, description, rating
        findViewById<TextView>(R.id.locationName).text = ellipsizeText(place.name)
        findViewById<TextView>(R.id.locationPrice).text = place.priceConversion()
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))

        // Update website and phone
        updateWebsite(response!!.results.website)
        updatePhone(response.results.formatted_phone_number)

        // Update reviews
        if(response.results.reviews != null) { // Reviews array is not empty
            val input = response.results.reviews[0]
            findViewById<TextView>(R.id.locationReviews).text = input.text
            findViewById<TextView>(R.id.locationReviewAuthor).text = ellipsizeText(input.author_name)
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                .getDrawable(this, ratingConversion(input.rating)))
            copyReviews(response.results.reviews)
        }
        else { // Reviews array empty
            findViewById<TextView>(R.id.locationReviews).text = resources.getString(R.string.default_review)
            findViewById<TextView>(R.id.locationReviewAuthor).text = resources.getString(R.string.default_review_author)
            findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat
                    .getDrawable(this, R.drawable.default_star))
        }
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

    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<Reviews>?) {
        for(i in 0..(input!!.size))
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

    class DetailsResponse(val results:DetailsResults, val status:String)

    class DetailsResults(val formatted_phone_number:String = "",
                         val reviews:List<Reviews>, val website:String = "")

    @Parcelize
    class Reviews(val author_name:String = "", val text:String = "",
                  val rating:Int = 0) : Parcelable

    // Builds URL for Place Details API call
    private
    fun detailsSearchUrlBuilder() : String {
        return "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + place.placeID +
                "&key=" + getString(R.string.google_api_key)
    }

    // Calls Place Details API
    private
    fun callDetailsAPI() : DetailsResponse? {
        Log.d("STREAM", detailsSearchUrlBuilder())
        return Klaxon().parse<DetailsResponse>(URL(detailsSearchUrlBuilder()).readText())
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
            res = input.substring(0, MAX_LENGTH) + "..."

        return res
    }
}
