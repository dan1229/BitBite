package com.example.daniel.bitbite

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity(), TopCard.OnFragmentInteractionListener,
    MoreInfoCard.OnFragmentInteractionListener, BottomCard.OnFragmentInteractionListener {

    /** Variables **/
    lateinit var user: MainActivity.User
    lateinit var place:Place
    var favorites = false


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get intent extras
        place = intent.getParcelableExtra("PLACE")
        user = intent.getParcelableExtra("USER")

        // Update toolbar title
        toolbar_location.title = ellipsizeText(place.name, 30)

        // Check if in Favorites
        favorites = favoritesContains(this, place.placeID)

        // Add fragments
        addTopCardFragment()
        addBottomCardFragment()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    // addTopCardFragment()
    // Adds TopCard Fragment to LocationActivity
    private
    fun addTopCardFragment() {
        val tfragment = TopCard.newInstance(place)
        fragmentManager.beginTransaction().add(R.id.location_topcard_container, tfragment).commit()
    }

    // addBottomCardFragment()
    // Adds BottomCard Fragment to LocationActivity
    private
    fun addBottomCardFragment() {
        val bfragment = BottomCard.newInstance(place, user)
        fragmentManager.beginTransaction().replace(R.id.location_bottomcard_container, bfragment).commit()
    }

    // onFragmentInteraction()
    // Mandatory implementation for interface (Bottom and MoreInfo)
    override fun onFragmentInteraction(fave: Boolean) {
        favorites = fave
    }

    // onFragmentInteraction()
    // Mandatory implementation for interface (Top)
    override fun onFragmentInteraction(uri: Uri) {
        //
    }


    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when MainActivity.kt is paused
    override fun onPause() {
        super.onPause()

        // Update Favorites list
        //updateFavorites(this, place, favorites)
    }

    // onResume()
    // Handles when MainActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Location onResume()")

        // Remake top and bottom card fragments
        addTopCardFragment()
        addBottomCardFragment()
    }


} /** END LocationActivity.kt **/
