package com.example.daniel.bitbite

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity(), ReviewCard.OnFragmentInteractionListener {

    /** Variables **/
    var reviews = ArrayList<Reviews>(5)
    var placeId = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get Extras
        reviews = intent.getParcelableArrayListExtra<Reviews>("review_list")
        placeId = intent.getStringExtra("place_id")
        name = intent.getStringExtra("name")

        // Update make your own review card
        placeName.visibility = View.VISIBLE
        placeName.text = name

        for(i in 0 until reviews.size){
            val fragment = ReviewCard.newInstance(reviews[i])
            fragmentManager.beginTransaction().add(R.id.review_container, fragment).commit()
        }

        // Set on click listener for making review
        makeReviewCard.setOnClickListener {
            if(placeId != "")
                openWebPage(this, mapsReviewUrlBuilder(placeId))
        }
    }


    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Mandatory implementation for interface
    override fun onFragmentInteraction(uri: Uri) {
        //
    }

} /** END CLASS ReviewActivity.kt **/
