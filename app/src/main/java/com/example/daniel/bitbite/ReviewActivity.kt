package com.example.daniel.bitbite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_review.*
import org.jetbrains.anko.toast

class ReviewActivity : AppCompatActivity() {

    /** Variables **/
    var reviews = ArrayList<Reviews>(5)
    var placeId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get reviews ArrayList
        val bundle = intent.getBundleExtra("myBundle")
        reviews = bundle.getParcelableArrayList<Reviews>("review_list")

        // Get placeID
        placeId = intent.getStringExtra("place_id")

        // Populate review cards
        for(i in 0..4) {
            updateReview(i)
        }

        // Set on click listener for making review
        makeReviewCard.setOnClickListener {
            openWebPage(this, mapsReviewUrlBuilder(placeId))
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateReview
    private
    fun updateReview(index : Int) {
        if(index >= reviews.size) { // Make card invisible
            changeCardVisibility(index)
        } else { // Populate card
            updateText(index)
            updateAuthor(index)
            updateRating(index)
        }
    }

    // changeCardVisibility()
    // Changes card visibility if review doesn't exist
    private
    fun changeCardVisibility(index : Int) {
        when (index) {
            0 -> findViewById<CardView>(R.id.reviewCard1).visibility = View.GONE
            1 -> findViewById<CardView>(R.id.reviewCard2).visibility = View.GONE
            2 -> findViewById<CardView>(R.id.reviewCard3).visibility = View.GONE
            3 -> findViewById<CardView>(R.id.reviewCard4).visibility = View.GONE
            4 -> findViewById<CardView>(R.id.reviewCard5).visibility = View.GONE
            else -> findViewById<CardView>(R.id.reviewCard1).visibility = View.GONE
        }
    }

    // updateText()
    private
    fun updateText(index : Int) {
        val view = getReviewTextView(index)
        val s = """"""" + reviews[index].text + """""""
        view.text = s
    }

    // updateAuthor()
    private
    fun updateAuthor(index : Int) {
        val view = getReviewAuthorView(index)
        val s = ellipsizeText("- ${reviews[index].author_name}", 25)
        view.text = s
    }

    // updateRating()
    private
    fun updateRating(index : Int) {
        val view = getReviewRatingView(index)
        view.setImageDrawable(ContextCompat.getDrawable(
                this, ratingConversion(reviews[index].rating)))
    }

    /**====================================================================================================**/
    /** View Finder Methods **/

    // getReviewTextView()
    private
    fun getReviewTextView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewReview1)
        1 -> findViewById(R.id.reviewReview2)
        2 -> findViewById(R.id.reviewReview3)
        3 -> findViewById(R.id.reviewReview4)
        4 -> findViewById(R.id.reviewReview5)
        else -> findViewById<TextView>(R.id.reviewReview1)
    }

    // getReviewAuthorView()
    private
    fun getReviewAuthorView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewAuthor1)
        1 -> findViewById(R.id.reviewAuthor2)
        2 -> findViewById(R.id.reviewAuthor3)
        3 -> findViewById(R.id.reviewAuthor4)
        4 -> findViewById(R.id.reviewAuthor5)
        else -> findViewById<TextView>(R.id.reviewAuthor1)
    }

    // getReviewRatingView()
    private
    fun getReviewRatingView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewRating1)
        1 -> findViewById(R.id.reviewRating2)
        2 -> findViewById(R.id.reviewRating3)
        3 -> findViewById(R.id.reviewRating4)
        4 -> findViewById(R.id.reviewRating5)
        else -> findViewById<ImageView>(R.id.reviewRating1)
    }

    /**====================================================================================================**/
    /** Helper Methods **/

    // openWebPage()
    // Opens web page to passed URL
    private
    fun openWebPage(context : Context, string : String) {
        val uris = Uri.parse(string)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        context.startActivity(intents)
    }

    // mapsReviewUrlBuilder()
    // Builds URL to leave Google Maps Review
    private
    fun mapsReviewUrlBuilder(id : String) : String {
        return "https://search.google.com/local/writereview?placeid=$id"
    }

} /** END CLASS ReviewActivity.kt **/
