package com.example.daniel.bitbite

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : BaseActivity(), ReviewCard.OnFragmentInteractionListener {

    /** Variables **/
    var reviews = ArrayList<Reviews>(5)
    var placeId = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Setup Toolbar
        toolbarBuilderUpNavLogo(review_toolbar)

        // Get Extras
        reviews = intent.getParcelableArrayListExtra<Reviews>("review_list")
        placeId = intent.getStringExtra("place_id")
        name = intent.getStringExtra("name")

        // Update make your own review card
        addMakeReviewCard()

        for(i in 0 until reviews.size){
            val fragment = ReviewCard.newInstance(reviews[i])
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right)
                    .add(R.id.review_container, fragment).commit()
        }
    }


    /**====================================================================================================**/
    /** Updater Methods **/

    // addMakeReviewCard()
    // Adds make your own review card at the top
    private
    fun addMakeReviewCard() {
        // Make Review object
        val review = Reviews(name, "EMPTY", getString(R.string.make_review_description), 0)

        // Add fragment
        val frag = ReviewCard.newInstance(review, 2)
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_right)
                .add(R.id.review_top_container, frag).commit()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Launches reviews page when clicked on top card
    override fun onFragmentInteraction() {
        if(placeId != "")
            openWebPage(mapsReviewUrlBuilder(placeId))
    }

} /** END CLASS ReviewActivity.kt **/
