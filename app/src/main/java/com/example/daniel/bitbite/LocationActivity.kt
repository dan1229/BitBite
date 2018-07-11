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
    lateinit var user: MainActivity.User
    var distance = Pair("", "")
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

        // Set on click listener for More Info Button -> makes views visible
        locationButtonMoreinfo.setOnClickListener {
            goToMoreInfo()
        }

        // Set on click listener for Directions Button -> Google Maps
        locationButtonDirections.setOnClickListener{
            place.openMapsPage(this)
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
        intent.putExtra("image_height", locationImage.height) // Pass image height

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(this@LocationActivity,
                    Pair.create<View, String>(locationTopCard, "top_card"),
                    Pair.create<View, String>(locationImage, "place_image"))
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

} /** END LocationActivity.kt **/
