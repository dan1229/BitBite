package com.example.daniel.bitbite

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class LocationActivity : AppCompatActivity() {

    /** Variables **/
    lateinit var place:Place
    lateinit var response:DetailsResponse
    var reviews = ArrayList<Reviews>(5)
    var website = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get place
        place = intent.getParcelableExtra("place")

        // Update photo
        updatePhoto(place.photoRef)

        // Place Details call
        doAsync {
            response = callDetailsAPI(this@LocationActivity, place) as DetailsResponse

            uiThread {
                // Populate Location card
                updateLocation(response)
            }
        }


        // Set on click listener for Reviews -> ReviewActivity.kt
        layoutReviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.size > 0) {
                goToReviews()
            }
            else {
                toast("No reviews available.")
            }
        }

        // Set on click listener for Website -> Web Browser
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

        // Set on click listener for Phone Number -> Dialer
        layoutPhone.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
        }

        // Set on click listener for Directions Button -> Google Maps
        buttonDirections.setOnClickListener{
            place.openMapsPage(this)
        }

        // Set on click listener for Share Button -> Share Menu
        buttonShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, place.googleMapsUrl);
            startActivity(shareIntent)
        }
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToReviews()
    // Creates Intent for Reviews.kt and animates transition
    private
    fun goToReviews() {
        // Create Intent
        val intent = Intent(this@LocationActivity, ReviewActivity::class.java)
        intent.putParcelableArrayListExtra("review_list", reviews)
        intent.putExtra("place_id", place.placeID)

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@LocationActivity, layoutReviews, "review_card")
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateLocation()
    private
    fun updateLocation(response : DetailsResponse?) {

        // Photo and Opennow
        updatePhoto(place.photoRef)
        updateOpennow(place.openNow)
        updatePrice(place)

        // Name, price, description, rating
        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))

        // Update website, phone and review
        updateWebsite(response!!.result.website)
        updatePhone(response.result.formatted_phone_number)
        updateReviews(response.result.reviews)
    }

    // updatePhoto()
    private
    fun updatePhoto(photoRef : String){
        if(photoRef != "DEFAULT")
            place.placePhotoCall(this, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))
    }

    // updateOpennow()
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

    // updatePrice()
    private
    fun updatePrice(place : Place) {
        val view = findViewById<TextView>(R.id.locationPrice)
        view.text = place.priceConversion()
    }

    // updateWebsite()
    private
    fun updateWebsite(input : String) {
        val view = findViewById<TextView>(R.id.locationWebsite)
        if(!input.equals(""))
            view.text = ellipsizeText(input, 30)
        else
            view.text = resources.getString(R.string.default_website)
        view.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        website = input
    }

    // updatePhone()
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

    // updateReviews()
    private
    fun updateReviews(reviews : List<Reviews>) {
        if(!reviews.isEmpty()) { // Reviews array is not empty
            setNonDefaultReview(response.result.reviews[0])
            copyReviews(response.result.reviews)
        }
        else { // Reviews array empty
            setDefaultReview()
        }
    }

    /**====================================================================================================**/
    /** Helper Methods **/

    // setNonDefaultReview()
    // Sets non-default review info
    private
    fun setNonDefaultReview(input : Reviews) {

        var s = """"""" + input.text + """""""
        findViewById<TextView>(R.id.locationReviews).text = s

        s = "- " + ellipsizeText(input.author_name)
        findViewById<TextView>(R.id.locationReviewAuthor).text = s

        findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat.getDrawable(this, ratingConversion(input.rating)))
    }

    // setDefaultReview()
    // Sets default review info
    private
    fun setDefaultReview() {
        findViewById<TextView>(R.id.locationReviews).text = resources.getString(R.string.default_review)
        findViewById<TextView>(R.id.locationReviewAuthor).text = resources.getString(R.string.default_review_author)
        findViewById<ImageView>(R.id.locationReviewRating).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_star))
    }

    // copyReviews()
    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<Reviews>?) {
        for(i in 0..(input!!.size - 1))
            reviews.add(input[i])
    }


} /** END LocationActivity.kt **/
