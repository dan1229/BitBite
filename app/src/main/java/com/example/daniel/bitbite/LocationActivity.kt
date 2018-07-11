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
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import android.util.Pair
import org.jetbrains.anko.toolbar


class LocationActivity : AppCompatActivity() {

    /** Variables **/
    lateinit var distance : Pair<String, String>
    lateinit var user: MainActivity.User
    lateinit var place:Place
    var favorites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get intent extras
        place = intent.getParcelableExtra("place")
        user = intent.getParcelableExtra("user")

        // Update toolbar title
        toolbar_location.title = ellipsizeText(place.name, 30)

        // Update photo
        placeUpdates()

        // Place Details call
        doAsync {
            distance = callDistanceApi(this@LocationActivity, user.lat, user.lng, place.placeID)

            uiThread { // Populate Location card
                distanceUpdates(distance.first, distance.second)
            }
        }

        // check if in favorites

        /**====================================================================================================**/
        /** On Click Listeners **/

        // Set on click listener for Favorites -> Add to favorites
        layoutFavorite.setOnClickListener {
            if(!favorites) {
                // add to favorites
                toast("Added ${place.name} to your Favorites!")
                favorites = true
                updateFavorites()
            }
        }

        // Set on click listener for Directions Button -> Google Maps
        buttonDirections.setOnClickListener{
            place.openMapsPage(this)
        }

        // Set on click listener for More Info Button -> makes views visible
        buttonMoreInfo.setOnClickListener {
            goToMoreInfo()
        }

    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToMoreInfo()
    // Creates Intent for MoreInfo.kt and animates transition
    private
    fun goToMoreInfo() {
        // Create Intent
        val intent = Intent(this@LocationActivity, MoreInfoActivity::class.java)
        intent.putExtra("place", place) // Pass place
        intent.putExtra("fave", favorites) // Pass favorites
        intent.putExtra("distance", distance.first) // Pass distance
        intent.putExtra("duration", distance.second) // Pass distance

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(this@LocationActivity,
                    Pair.create<View, String>(locationTopCard, "top_card"),
                    Pair.create<View, String>(locationIconLayout, "icon_layout"))
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // placeUpdates()
    // Updates fields from place object (Place Search)
    private
    fun placeUpdates() {
        // Top card updates
        updatePhoto(place.photoRef)
        updateOpennow(place.openNow)
        updatePrice(place)
        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(
                ContextCompat.getDrawable(this, place.ratingConversion()))

        // Bottom card updates
        updateFavorites()
        updateClock(place.openNow)
    }

    // distanceUpdates()
    // Updates fields related to disatnce
    private
    fun distanceUpdates(distance : String, duration : String) {
        if(distance != "")
            locationDistance.text = distance
        if(duration != "")
            locationDuration.text = duration
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

    // updateClock()
    private
    fun updateClock(bool : Boolean) {
        if(bool){
            findViewById<TextView>(R.id.locationClock).text = getString(R.string.open)
            findViewById<TextView>(R.id.locationClock).setTextColor(
                    ContextCompat.getColor(this, R.color.green))
        } else {
            findViewById<TextView>(R.id.locationClock).text = getString(R.string.closed)
            findViewById<TextView>(R.id.locationClock).setTextColor(
                    ContextCompat.getColor(this, R.color.red))
        }
    }

    // updatePrice()
    private
    fun updatePrice(place : Place) {
        val view = findViewById<TextView>(R.id.locationPrice)
        view.text = place.priceConversion()
    }

    // updateFavorites()
    private
    fun updateFavorites() {
        val view = findViewById<TextView>(R.id.locationFavorites)

        if(!favorites) { // If not in favorites
            view.text = getString(R.string.default_favorites)
        } else {
            locationFavoritesIcon.setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.favorites_filled_icon))
            view.text = getString(R.string.default_already_favorited)
        }
    }

} /** END LocationActivity.kt **/
