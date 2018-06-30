package com.example.daniel.bitbite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_review.*
import org.jetbrains.anko.toast

class ReviewActivity : AppCompatActivity() {

    var reviews = ArrayList<LocationActivity.Reviews>(5)
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)


        // Get reviews ArrayList
        val bundle = intent.getBundleExtra("myBundle")
        reviews = bundle.getParcelableArrayList<LocationActivity.Reviews>("review_list")
                //as ArrayList<LocationActivity.Reviews>

        // Set on click listener for next button
        reviewNext.setOnClickListener {
            if(index <= reviews.size) { // Can go forward
                ++index
                updateReview(index)
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
        var view = findViewById<TextView>(R.id.reviewReview)
        view.text = reviews[index].text
    }

    // Updates review author
    private
    fun updateAuthor(index : Int) {
        var view = findViewById<TextView>(R.id.reviewAuthor)
        view.text = reviews[index].author_name
    }

    // Updates review rating
    private
    fun updateRating(index : Int) {
        var view = findViewById<ImageView>(R.id.reviewRating)
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(
                this, ratingConversion(reviews[index].rating)))
    }

    // Converts rating to drawable of stars based on value
    private
    fun ratingConversion(rating : Int) = when (rating) {
        1 -> R.drawable.star_1
        2 -> R.drawable.star_2
        3 -> R.drawable.star_3
        4 -> R.drawable.star_4
        5 -> R.drawable.star_5
        else -> R.drawable.default_star
    }
}
