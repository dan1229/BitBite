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
    var height = 0


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get intent extras
        place = intent.getParcelableExtra("PLACE")
        user = intent.getParcelableExtra("USER")

        // Update toolbar title
        toolbar_location.title = ellipsizeText(place.name, 30)

        // Add top card fragment
        val tfragment = TopCard.newInstance(place)
        fragmentManager.beginTransaction().add(R.id.location_topcard_container, tfragment).commit()

        // Add bottom card fragment
        val bfragment = BottomCard.newInstance(place, user)
        fragmentManager.beginTransaction().replace(R.id.location_bottomcard_container, bfragment).commit()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

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
        if (favorites) { // Selected to be in favorites
            // check if in list, add if not
        } else { // Selected to not be in favorites
           // check if in list, remove if so
        }
    }

    // onResume()
    // Handles when MainActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Location onResume()")
    }


} /** END LocationActivity.kt **/
