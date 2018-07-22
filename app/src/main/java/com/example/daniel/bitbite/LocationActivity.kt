package com.example.daniel.bitbite

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Pair
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : BaseActivity(), TopCard.OnFragmentInteractionListener,
    MoreInfoCard.OnFragmentInteractionListener, BottomCard.OnFragmentInteractionListener,
    OtherLocationCard.OnFragmentInteractionListener {

    /** Variables **/
    var distance = ""
    var duration = ""
    lateinit var place: Place
    var favorites = false
    var index = 0
    lateinit var topCard: TopCard
    lateinit var bottomCard: BottomCard
    var moreInfoCard: MoreInfoCard? = null


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get Place
        place = intent.getParcelableExtra("PLACE")

        // Get distance pair
        distance = intent.getStringExtra("DISTANCE")
        duration = intent.getStringExtra("DURATION")

        // Update toolbar title
        toolbarBuilderUpNavLogo(location_toolbar)

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
        topCard = tfragment
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top)
                .replace(R.id.location_topcard_container, tfragment).commit()
    }

    // addBottomCardFragment()
    // Adds BottomCard Fragment to LocationActivity
    private
    fun addBottomCardFragment() {
        if(moreInfoCard == null) {
            val bfragment = BottomCard.newInstance(place, user, distance, duration)
            bfragment.index = index

            bottomCard = bfragment
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .replace(R.id.location_bottomcard_container, bfragment).commit()
        } else{
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .replace(R.id.location_bottomcard_container, moreInfoCard).commit()
        }
    }

    // fragmentFavoritesChanged()
    // Mandatory implementation for interface (Bottom and MoreInfo)
    override fun fragmentFavoritesChanged(fave: Boolean) {
        favorites = fave
    }

    // fragmentFavoritesChanged()
    // Mandatory implementation for interface (Top)
    override fun onFragmentInteraction(uri: Uri) {
        //
    }

    // onMoreInfoCreation()
    // Returns moreInfoCard when created
    override fun onMoreInfoCreation(frag: MoreInfoCard) {
        moreInfoCard = frag
    }

    // otherLocationFragmentSelected()
    // Handles clicks on OtherLocationCard fragments
    override fun otherLocationFragmentSelected(place: Place, distance: Pair<String, String>) {
        goToLocation(place, distance.first, distance.second)
    }


    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when MainActivity.kt is paused
    override fun onPause() {
        super.onPause()

        // Update Favorites list
        updateFavorites(this, place, favorites)
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
