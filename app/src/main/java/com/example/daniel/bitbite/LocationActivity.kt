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
        place = intent.getParcelableExtra("place")
        user = intent.getParcelableExtra("user")

        // Update toolbar title
        toolbar_location.title = ellipsizeText(place.name, 30)

        // Add top card fragment
        val tfragment = TopCard.newInstance(place)
        fragmentManager.beginTransaction().add(R.id.location_topcard_container, tfragment).commit()

        // Add bottom card fragment
        val bfragment = BottomCard.newInstance(place, user)
        fragmentManager.beginTransaction().add(R.id.location_bottomcard_container, bfragment).commit()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Mandatory implementation for interface
    override fun onFragmentInteraction(uri: Uri) {
        //
    }

} /** END LocationActivity.kt **/
