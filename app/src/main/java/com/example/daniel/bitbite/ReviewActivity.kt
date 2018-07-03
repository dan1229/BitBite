package com.example.daniel.bitbite

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

    var reviews = ArrayList<Reviews>(5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get reviews ArrayList
        val bundle = intent.getBundleExtra("myBundle")
        reviews = bundle.getParcelableArrayList<Reviews>("review_list")

        // Populate review cards
        Log.d(("INDEX"), reviews.size.toString())
        for(i in 0..4) {
            updateReview(i)
        }
    }

    // Updates review card
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

    // Updates review text
    private
    fun updateText(index : Int) {
        val view = getReviewTextView(index)
        val s = """"""" + reviews[index].text + """""""
        view.text = s
    }

    // Updates review author
    private
    fun updateAuthor(index : Int) {
        val view = getReviewAuthorView(index)
        val s = ellipsizeText("- ${reviews[index].author_name}", 25)
        view.text = s
    }

    // Updates review rating
    private
    fun updateRating(index : Int) {
        val view = getReviewRatingView(index)
        view.setImageDrawable(ContextCompat.getDrawable(
                this, reviewRatingConversion(reviews[index].rating)))
    }

    // Gets TextView for review text view
    private
    fun getReviewTextView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewReview1)
        1 -> findViewById(R.id.reviewReview2)
        2 -> findViewById(R.id.reviewReview3)
        3 -> findViewById(R.id.reviewReview4)
        4 -> findViewById(R.id.reviewReview5)
        else -> findViewById<TextView>(R.id.reviewReview1)
    }

    // Gets TextView for review author view
    private
    fun getReviewAuthorView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewAuthor1)
        1 -> findViewById(R.id.reviewAuthor2)
        2 -> findViewById(R.id.reviewAuthor3)
        3 -> findViewById(R.id.reviewAuthor4)
        4 -> findViewById(R.id.reviewAuthor5)
        else -> findViewById<TextView>(R.id.reviewAuthor1)
    }


    // Gets ImageView for review rating view
    private
    fun getReviewRatingView(card : Int) = when(card) {
        0 -> findViewById(R.id.reviewRating1)
        1 -> findViewById(R.id.reviewRating2)
        2 -> findViewById(R.id.reviewRating3)
        3 -> findViewById(R.id.reviewRating4)
        4 -> findViewById(R.id.reviewRating5)
        else -> findViewById<ImageView>(R.id.reviewRating1)
    }

    // Converts rating to drawable of stars based on value
    private
    fun reviewRatingConversion(rating : Int) = when (rating) {
        1 -> R.drawable.star_1
        2 -> R.drawable.star_2
        3 -> R.drawable.star_3
        4 -> R.drawable.star_4
        5 -> R.drawable.star_5
        else -> R.drawable.default_star
    }

    // Ellipsizes text
    private
    fun ellipsizeText(input : String, max : Int = 20) : String {
        val size = input.length
        var res = input

        if (size > max)
            res = input.substring(0, max - 3) + "..."

        return res
    }
}
