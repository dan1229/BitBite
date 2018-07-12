package com.example.daniel.bitbite

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import android.util.Pair
import android.view.View


class LocationActivity : AppCompatActivity(), TopCard.OnFragmentInteractionListener,
    MoreInfoCard.OnFragmentInteractionListener {

    /** Variables **/
    lateinit var user: MainActivity.User
    var distance = Pair("", "")
    lateinit var place:Place
    var favorites = false
    var height = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get intent extras
        place = intent.getParcelableExtra("place")
        user = intent.getParcelableExtra("user")

        // Update toolbar title
        toolbar_location.title = ellipsizeText(place.name, 30)

        // Add top card fragment
        val fragment = TopCard.newInstance(place)
        fragmentManager.beginTransaction().add(R.id.location_topcard_container, fragment).commit()

        // Update Location card
        locationUpdates()

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
                // favorites = updateFavoritesList(place)
                toast("Added ${place.name} to your Favorites!")
                favorites = true
            } else { // In favorites - remove
                // remove from favorites
                toast("Removed ${place.name} from your Favorites!")
                favorites = false
            }
            updateFavorites()
        }

        // Set on click listener for More Info Button -> makes views visible
        locationButtonMoreinfo.setOnClickListener {
            createMoreInfoFragment()
        }

        // Set on click listener for Directions Button -> Google Maps
        locationButtonDirections.setOnClickListener{
            place.openMapsPage(this)
        }
    }

    /**====================================================================================================**/
    /** Fragment Makers **/

    // createMoreInfoFragment()
    // Creates MoreInfoCard fragment and adds to container
    private
    fun createMoreInfoFragment() {
        val fragment = MoreInfoCard.newInstance(place, favorites, distance.first, distance.second)
        fragmentManager.beginTransaction().add(R.id.moreinfocard_container, fragment).commit()
        locationCard.visibility = View.GONE
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // locationUpdates()
    // Updates Location card fields
    private
    fun locationUpdates() {
        updateFavorites()
        updateClock(place.openNow)
    }

    // distanceUpdates()
    // Updates fields related to distance
    private
    fun distanceUpdates(distance : String, duration : String) {
        if(distance != "")
            locationDistance.text = distance
        if(duration != "")
            locationDuration.text = duration
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

    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Mandatory implementation for interface
    override fun onFragmentInteraction(uri: Uri) {
        //
    }

} /** END LocationActivity.kt **/
