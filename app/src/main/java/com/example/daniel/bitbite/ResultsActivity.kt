package com.example.daniel.bitbite

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_results.*
import org.jetbrains.anko.toast
import com.example.daniel.bitbite.R.id.toolbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.w3c.dom.Text


class ResultsActivity : AppCompatActivity() {

    var places = ArrayList<Place>()
    var listSize = 0
    var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        toolbar_results.title = "Results"
        setSupportActionBar(toolbar_results)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get places ArrayList
        val bundle = intent.getBundleExtra("myBundle")
        places = bundle.getParcelableArrayList<Place>(EXTRA_PLACES_LIST) as ArrayList<Place>
        listSize = places.size

        // Initial card update
        for(i in 0..2){
            updateCard(i + 1, i)
        }

        // Set on click listener for "Next 3" button
        displayNext3.setOnClickListener {
            if ((3 * (page + 1)) < listSize) { // Display next page
                ++page
                for (i in 0..2) {
                    updateCard(i + 1, 3 * page + i)
                }
            }
            else { // Can't go forward, display error
                toast("End of list - can't go further")
            }
        }

        // Set on click listener for "Go back" button
        displayPrev3.setOnClickListener {
            if (page == 0) { // Can't go back, display error
                toast("Beginning of list - can't go back further")
            }
            else { // Display prev page
                --page
                for (i in 0..2) {
                    updateCard(i + 1, 3 * page + i)
                }
            }
        }


        // Set on click listeners for cards to send to Google Maps
        card1.setOnClickListener {
            if ((3 * page) < listSize) {
                goToLocation(3 * page)
            }
        }

        card2.setOnClickListener {
            if ((3 * page + 1) < listSize) {
                goToLocation(3 * page + 1)
            }
        }

        card3.setOnClickListener {
            if ((3 * page + 2) < listSize) {
                goToLocation(3 * page + 2)
            }
        }
    }

    // Goes to LocationActivity, calls Place Details API
    private
    fun goToLocation(index : Int) {
        doAsync {

            uiThread {
                val intent = Intent(this@ResultsActivity, LocationActivity::class.java)
                intent.putExtra("location", places[index])
                startActivity(intent)
            }
        }
    }

    // Calls update function for each segment of card
    private
    fun updateCard(card : Int, index : Int) {
        var i = index
        if(i >= listSize) { // Check if index is in bounds
            i = -1
        }
        updateName(card, i)
        updatePrice(card, i)
        updatePhoto(card, i)
        updateRating(card, i)
        updateDescription(card, i)
    }

    // Updates name on the card
    private
    fun updateName(card : Int, index : Int) {
        val textView = getNameView(card)
        if(index >= 0){ // In bounds
            textView.text = ellipsizeText(places[index].name)
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_name)
        }
    }

    // Updates price on the card
    private
    fun updatePrice(card : Int, index : Int) {
        val textView = getPriceView(card)
        if(index >= 0){ // In bounds
            textView.text = places[index].priceConversion()
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_price)
        }
    }

    // Updates photo on the card
    private
    fun updatePhoto(card : Int, index : Int) {
        val view = getPhotoView(card)
        if((index >= 0) && (places[index].photoRef != "DEFAULT")){ // In bounds
            placePhotoCall(places[index].photoRef, view)
        } else{ // Out of bounds, default photo
            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_place_image))
        }
    }

    // Updates rating on the card
    private
    fun updateRating(card : Int, index : Int) {
        val view = getRatingView(card)
        if(index >= 0){ // In bounds
            view.setImageDrawable(ContextCompat.getDrawable(this, places[index].ratingConversion()))
        } else{ // Out of bounds, default photo
            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_star))
        }
    }

    // Updates description on the card
    private
    fun updateDescription(card : Int, index : Int) {
        val textView = getDescriptionView(card)
        if(index >= 0){ // In bounds
            textView.text = places[index].fixDescription()
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_description)
        }
    }

    // Gets TextView for name view based on card number
    private
    fun getNameView(card : Int) = when(card) {
            1 -> findViewById(R.id.name1)
            2 -> findViewById(R.id.name2)
            3 -> findViewById(R.id.name3)
            else -> findViewById<TextView>(R.id.name1)
    }

    // Gets TextView for price view based on card number
    private
    fun getPriceView(card : Int) = when(card) {
            1 -> findViewById(R.id.price1)
            2 -> findViewById(R.id.price2)
            3 -> findViewById(R.id.price3)
            else -> findViewById<TextView>(R.id.price1)
    }

    // Gets ImageView for photo view based on card number
    private
    fun getPhotoView(card : Int) = when(card) {
            1 -> findViewById(R.id.image1)
            2 -> findViewById(R.id.image2)
            3 -> findViewById(R.id.image3)
            else -> findViewById<ImageView>(R.id.image1)
    }

    // Gets TextView for rating view based on card number
    private
    fun getRatingView(card : Int) = when(card) {
            1 -> findViewById(R.id.rating1) as ImageView
            2 -> findViewById(R.id.rating2) as ImageView
            3 -> findViewById(R.id.rating3) as ImageView
            else -> findViewById(R.id.rating1) as ImageView
    }

    // Gets TextView for descritpion view based on card number
    private
    fun getDescriptionView(card : Int) = when(card) {
            1 -> findViewById(R.id.description1) as TextView
            2 -> findViewById(R.id.description2) as TextView
            3 -> findViewById(R.id.description3) as TextView
            else -> findViewById(R.id.description1) as TextView
    }

    // Calls Place Photo API and returns image
    private
    fun placePhotoCall(ref : String, view : ImageView) {
        Glide.with(this).load(createPhotosRequestURL(ref)).into(view)
    }

    // Creates URL for Place Photo API request
    private
    fun createPhotosRequestURL(ref : String) : String {
        return  "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=1000" +
                "&photoreference=" + ref +
                "&key=" + getString(R.string.google_api_key)
    }

    // Ellipsizes text
    private
    fun ellipsizeText(input : String) : String {
        val MAX_LENGTH = 20
        var size = input.length
        var res = input

        if (size > MAX_LENGTH)
            res = input.substring(0, MAX_LENGTH) + "..."

        return res
    }

} // END CLASS ResultsActivity.kt
