package com.example.daniel.bitbite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_review.*
import org.jetbrains.anko.toast

class ReviewActivity : AppCompatActivity() {

    var reviews = ArrayList<Reviews>(5)
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Get reviews ArrayList
        val bundle = intent.getBundleExtra("myBundle")
        reviews = bundle.getParcelableArrayList<Reviews>("review_list")

        // Initial update reviews list
        updateReview(index)
        ++index

        // Set on click listener for next button
        reviewNext.setOnClickListener {
            if(index < reviews.size) { // Can go forward
                updateReview(index)
                ++index
            } else { // Can't go forward
                toast("End of list - can't go further")
            }
        }

        // Set on click listener for prev button
        reviewPrev.setOnClickListener {
            if(index > 0){ // Can go back
                --index
                updateReview(index)
            } else { // Can't go back
                toast("Beginning of list - can't go back further")
            }
        }
    }

    // Updates review card
    private
    fun updateReview(index : Int) {
        updateText(index)
        updateAuthor(index)
        updateRating(index)
    }

    // Updates review text
    private
    fun updateText(index : Int) {
        val view = findViewById<TextView>(R.id.reviewReview)
        val s = """"""" + reviews[index].text + """""""
        view.text = s
    }

    // Updates review author
    private
    fun updateAuthor(index : Int) {
        val view = findViewById<TextView>(R.id.reviewAuthor)
        val s = "- ${reviews[index].author_name}"
        view.text = s
    }

    // Updates review rating
    private
    fun updateRating(index : Int) {
        val view = findViewById<ImageView>(R.id.reviewRating)
        view.setImageDrawable(ContextCompat.getDrawable(this, reviewRatingConversion(reviews[index].rating)))
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
}
