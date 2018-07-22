package com.example.daniel.bitbite

import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : BaseActivity(), MoreInfoCard.OnFragmentInteractionListener,
    BottomCard.OnFragmentInteractionListener,
    OtherLocationCard.OnFragmentInteractionListener {

    /** Variables **/
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

        if(place.duplicates.isNotEmpty())
            Log.d("OTHER", "DUPLICATES NOT EMPTY")

        // Update toolbar title
        toolbarBuilderUpNavLogo(location_toolbar)

        // Check if in Favorites
        favorites = favoritesContains(this, place.placeID)

        // Add fragments
        addTopCardFragment()
        addBottomCardFragment()
    }

    /**====================================================================================================**/
    /** Add Fragment Methods **/

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


    /**====================================================================================================**/
    /** Fragment Interaction Methods **/


    /** BottomCard **/
    // addMoreInfoCard()
    // Adds MoreInfoCard Fragment to LocationActivity
    override fun addMoreInfoCard() {
        val fragment = MoreInfoCard.newInstance(place, favorites, distance, duration, user)
        fragment.index = index

        fragmentManager!!.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                .replace(R.id.location_bottomcard_container, fragment).commit()
    }

    // fragmentFavoritesChanged()
    // Handles when Favorites is changed (Bottom and MoreInfo)
    override fun fragmentFavoritesChanged(fave: Boolean) {
        favorites = fave
    }

    // distanceCalled()
    // Stores distance and duration when API called
    override fun distanceCalled(dist: String, dur: String) {
        distance = dist
        duration = dur
    }

    /** MoreInfoCard **/
    // onReviewCardInteraction
    // Handles clicks on Reviews section
    override fun onReviewCardInteraction(reviews: ArrayList<Reviews>, place: Place) {
        goToReviews(reviews, place)
    }

    /** OtherLocationCard **/
    // otherLocationFragmentSelected()
    // Handles clicks on OtherLocationCard fragments
    override fun otherLocationFragmentSelected(dupIndex: Int, dist: String, dur: String) {
        distance = dist
        duration = dur
        finish()
        goToLocation(placesList[index].duplicates[dupIndex])
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
