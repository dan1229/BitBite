package com.example.daniel.bitbite

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_more_info.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import android.util.Pair
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.textColor

class MoreInfoActivity : AppCompatActivity() {

    /** Variables **/
    var reviews = ArrayList<Reviews>(5)
    lateinit var detailsResponse : DetailsResponse
    lateinit var place:Place
    var website = ""
    var phone = ""
    var favorites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        setSupportActionBar(toolbar_moreinfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get intent extras
        place = intent.getParcelableExtra("place")
        favorites = intent.getBooleanExtra("fave", false)

        // Update toolbar title
        toolbar_moreinfo.title = ellipsizeText(place.name, 30)

        // Update photo
        topCardUpdates()

        // Place Details call
        doAsync {
            detailsResponse = callDetailsApi(this@MoreInfoActivity, place) as DetailsResponse

            uiThread { // Populate Location card
                responseUpdates(detailsResponse)
            }
        }


        /**====================================================================================================**/
        /** On Click Listeners **/

        // Set on click listener for Reviews -> ReviewActivity.kt
        moreinfoLayoutReviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.isNotEmpty())
                goToReviews()
        }

        // Set on click listener for Website -> Web Browser
        moreinfoLayoutWebsite.setOnClickListener {
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
        moreinfoLayoutPhone.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
        }

        // Set on click listener for Address -> Google Maps
        moreinfoLayoutAddress.setOnClickListener {
            place.openMapsPage(this)
        }

        // Set on click listener for Favorites -> Add to favorites
        moreinfoFavorites.setOnClickListener {
            if(!favorites) { // Not in favorites - add
                // add to favorites
                toast("Added ${place.name} to your Favorites!")
                favorites = true
                updateFavorites()
            } else { // In favorites - remove
                // remove from favorites
                toast("Removed ${place.name} from your Favorites!")
                favorites = false
                updateFavorites()
            }
        }

        // Set on click listener for Directions Button -> Google Maps
        moreinfoButtonDirections.setOnClickListener{
            place.openMapsPage(this)
        }

        // Set on click listener for Share Button -> Share Menu
        moreinfoButtonShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey, look what I found using BitBite (https://BitBite.app) :\n\n" +
                            "${place.name}\n" +
                            detailsResponse.result.website)
            startActivity(shareIntent)
        }

        // Set on click listener for Call Button -> Dialer
        moreinfoButtonShare.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
        }
    }


    /**====================================================================================================**/
    /** Intent Makers **/

    // goToReviews()
    // Creates Intent for Reviews.kt and animates transition
    private
    fun goToReviews() {
        // Create Intent
        val intent = Intent(this@MoreInfoActivity, ReviewActivity::class.java)
        intent.putParcelableArrayListExtra("review_list", reviews) // Pass reviews
        intent.putExtra("place_id", place.placeID) // Pass placeID
        intent.putExtra("name", place.name) // Pass name

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(this@MoreInfoActivity,
                    Pair.create<View, String>(moreinfoLayoutReviews, "review_card"),
                    Pair.create<View, String>(moreinfoReviewsRating, "review_rating"),
                    Pair.create<View, String>(moreinfoReviewsText, "review_text"))
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // topCardUpdates()
    // Updates fields from place object (Place Search)
    private
    fun topCardUpdates() {
        // Top card updates
        updateOpennow(place.openNow)
        updatePrice(place)
        updateFavorites()
        updateClock(place.openNow)
        distanceUpdates(intent.getStringExtra("distance"), intent.getStringExtra("duration"))

        findViewById<TextView>(R.id.moreinfoName).text = place.name
        findViewById<TextView>(R.id.moreinfoDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.moreinfoRating).setImageDrawable(
                ContextCompat.getDrawable(this, place.ratingConversion()))
    }

    // responseUpdates()
    // Updates fields from detailsResponse object (Place Details)
    private
    fun responseUpdates(response : DetailsResponse?) {
        // Update website, phone and review
        updateWebsite(response!!.result.website)
        updatePhone(response.result.formatted_phone_number)
        updateAddress(response.result.formatted_address)
        updateReviews(response.result.reviews)
    }

    // distanceUpdates()
    // Updates fields related to disatnce
    private
    fun distanceUpdates(distance : String, duration : String) {
        if(distance != "")
            moreinfoDistance.text = distance
        if(duration != "")
            moreinfoDuration.text = duration
    }

    // updateOpennow()
    private
    fun updateOpennow(bool : Boolean) {
        if(bool){
            findViewById<TextView>(R.id.moreinfoOpennowText).text = getString(R.string.yes)
            findViewById<TextView>(R.id.moreinfoOpennowText).setTextColor(
                    ContextCompat.getColor(this, R.color.green))
        } else {
            findViewById<TextView>(R.id.moreinfoOpennowText).text = getString(R.string.no)
            findViewById<TextView>(R.id.moreinfoOpennowText).setTextColor(
                    ContextCompat.getColor(this, R.color.red))
        }
    }

    // updateClock()
    private
    fun updateClock(bool : Boolean) {
        if(bool){
            findViewById<TextView>(R.id.moreinfoClock).text = getString(R.string.open)
            findViewById<TextView>(R.id.moreinfoClock).setTextColor(
                    ContextCompat.getColor(this, R.color.green))
        } else {
            findViewById<TextView>(R.id.moreinfoClock).text = getString(R.string.closed)
            findViewById<TextView>(R.id.moreinfoClock).setTextColor(
                    ContextCompat.getColor(this, R.color.red))
        }
    }

    // updatePrice()
    private
    fun updatePrice(place : Place) {
        val view = findViewById<TextView>(R.id.moreinfoPrice)
        view.text = place.priceConversion()
    }

    // updateFavorites()
    private
    fun updateFavorites() {
        val view = findViewById<TextView>(R.id.moreinfoFavorites)

        if(!favorites) { // Not in favorites
            locationFavoritesIcon.setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.favorites_icon))
            view.text = getString(R.string.default_favorites)
            view.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        } else { // Already in favorites
            locationFavoritesIcon.setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.favorites_filled_icon))
            view.text = getString(R.string.default_already_favorited)
            view.setTextColor(ContextCompat.getColor(this, R.color.gold))
        }
    }

    // updateWebsite()
    private
    fun updateWebsite(input : String) {
        val view = findViewById<TextView>(R.id.moreinfoWebsite)
        if(!input.equals(""))
            view.text = input
        else
            view.text = resources.getString(R.string.default_website)
        website = input
    }

    // updatePhone()
    private
    fun updatePhone(input : String) {
        val view = findViewById<TextView>(R.id.moreinfoPhone)
        if(!input.equals(""))
            view.text = input
        else
            view.text = resources.getString(R.string.default_phone)
        phone = input
    }

    // updateAddress()
    private
    fun updateAddress(input : String) {
        val view = findViewById<TextView>(R.id.moreinfoAddress)
        if(input != "")
            view.text = input
    }

    // updateReviews()
    private
    fun updateReviews(reviews : List<Reviews>) {
        if(!reviews.isEmpty()) { // Reviews array is not empty
            setNonDefaultReview(detailsResponse.result.reviews[0])
            copyReviews(detailsResponse.result.reviews)
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
        findViewById<TextView>(R.id.moreinfoReviewsText).text = s

        s = "- " + ellipsizeText(input.author_name)
        findViewById<TextView>(R.id.moreinfoReviewsAuthor).text = s

        findViewById<ImageView>(R.id.moreinfoReviewsRating).setImageDrawable(ContextCompat.getDrawable(
                this, reviewRatingConversion(input.rating)))
    }

    // setDefaultReview()
    // Sets default review info
    private
    fun setDefaultReview() {
        findViewById<TextView>(R.id.moreinfoReviewsText).text = resources.getString(R.string.default_review)
        findViewById<TextView>(R.id.moreinfoReviewsAuthor).text = resources.getString(R.string.default_review_author)
        findViewById<ImageView>(R.id.moreinfoReviewsRating).setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.default_star))
    }

    // copyReviews()
    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<Reviews>?) {
        for(i in 0..(input!!.size - 1))
            reviews.add(input[i])
    }

} // END CLASS MoreInfoActivity.kt
