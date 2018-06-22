package com.example.daniel.digit

import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_results.*
import org.jetbrains.anko.image
import org.jetbrains.anko.toast

class ResultsActivity : AppCompatActivity() {

    var cardIndeces = IntArray(4)
    var places = ArrayList<Place>()
    var listSize = 0
    var lastButton = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        toolbar_results.title = "Results"
        setSupportActionBar(toolbar_results)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Get places ArrayList and size from MainActivity
        val bundle = intent.getBundleExtra("myBundle")
        places = bundle.getParcelableArrayList<Place>(EXTRA_PLACES_LIST) as ArrayList<Place>
        listSize = places.size

        // ArrayList index
        var index = -1

        // Initial card update
        var card = 1
        while((index < listSize) && (card <= 3)) {
            index = nextIndex(index)
            updateCard(card, index)
            ++card
        }

        // Set on click listener for "Next 3" button
        displayNext3.setOnClickListener {
            if (index < listSize) { // Display next page
                for (i in 1..3) {
                    index = nextIndex(index)
                    updateCard(i, index)
                }
            } else { // Can't go forward, display error
                toast("End of list - can't go further")
            }
        }

        // Set on click listener for "Go back" button
        displayPrev3.setOnClickListener {
            if (index >= 3) { // Display prev page
                for (i in 3 downTo 1) {
                    index = prevIndex(index)
                    updateCard(i, index)
                }
            }
            else { // Can't go back, display error
                toast("Beginning of list - can't go back further")
            }
        }

        // Set on click listeners for cards to send to Google Maps
        card1.setOnClickListener {
            if (cardIndeces[1] != -1)
                places[cardIndeces[1]].openWebPage(this)
        }

        card2.setOnClickListener {
            if (cardIndeces[2] != -1)
                places[cardIndeces[2]].openWebPage(this)
        }

        card3.setOnClickListener {
            if (cardIndeces[3] != -1)
                places[cardIndeces[3]].openWebPage(this)
        }
    }

    // Calls update function for each segment of card
    private
    fun updateCard(card : Int, index : Int) {
        var i = index
        if(index >= listSize) { // Check if index is in bounds
            i = -1
        }
        updateName(card, i)
        updatePrice(card, i)
        updatePhoto(card, i)
        updateRating(card, i)
        updateDescription(card, i)
        cardIndeces[card] = i
    }

    // Updates name on the card
    private
    fun updateName(card : Int, index : Int) {
        val textView = getNameView(card)
        if(index >= 0){ // In bounds
            textView.text = places[index].name
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_name)
        }
    }

    // Updates price on the card
    private
    fun updatePrice(card : Int, index : Int) {
        val textView = getPriceView(card)
        if(index >= 0){ // In bounds
            textView.text = priceConversion(places[index].price)
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_price)
        }
    }

    // Updates photo on the card
    private
    fun updatePhoto(card : Int, index : Int) {
        val view = getPhotoView(card)
        if((index >= 0) && (index < listSize)){ // In bounds
            placePhotoCall(places[index].photoRef, view, index)
        } else{ // Out of bounds, default photo
            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_place_image))
        }
    }

    // Updates rating on the card
    private
    fun updateRating(card : Int, index : Int) {
        val view = getRatingView(card)
        if(index >= 0){ // In bounds
            view.setImageDrawable(ContextCompat.getDrawable(this, ratingConversion(places[index].rating)))
        } else{ // Out of bounds, default photo
            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_star))
        }
    }

    // Updates description on the card
    private
    fun updateDescription(card : Int, index : Int) {
        val textView = getDescriptionView(card)
        if(index >= 0){ // In bounds
            textView.text = places[index].description.capitalize()
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_description)
        }
    }

    // Gets TextView for name view based on card number
    private
    fun getNameView(card : Int) = when(card) {
            1 -> findViewById(R.id.name1) as TextView
            2 -> findViewById(R.id.name2) as TextView
            3 -> findViewById(R.id.name3) as TextView
            else -> findViewById(R.id.name1) as TextView
    }

    // Gets TextView for price view based on card number
    private
    fun getPriceView(card : Int) = when(card) {
            1 -> findViewById(R.id.price1) as TextView
            2 -> findViewById(R.id.price2) as TextView
            3 -> findViewById(R.id.price3) as TextView
            else -> findViewById(R.id.price1) as TextView
    }

    // Gets ImageView for photo view based on card number
    private
    fun getPhotoView(card : Int) = when(card) {
            1 -> findViewById(R.id.image1) as ImageView
            2 -> findViewById(R.id.image2) as ImageView
            3 -> findViewById(R.id.image3) as ImageView
            else -> findViewById(R.id.image1) as ImageView
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

    // Converts price to string of "$" based on value
    private
    fun priceConversion(price : Int) = when(price) {
            1 -> "$"
            2 -> "$$"
            3 -> "$$$"
            4 -> "$$$$"
            5 -> "$$$$$"
            else -> ""
    }

    // Converts rating to string of stars based on value
    private
    fun ratingConversion(rating : Int) = when (rating) {
            1 -> R.drawable.star_1
            2 -> R.drawable.star_2
            3 -> R.drawable.star_3
            4 -> R.drawable.star_4
            5 -> R.drawable.star_5
            else -> R.drawable.default_star
    }

    // Calls Place Photo API and returns image
    private
    fun placePhotoCall(ref : String, view : ImageView, index : Int) {
        if(!ref.equals("DEFAULT")) {// If image ref exists, get it
            if(places[index].image == null) { // Image not already in object, download it
                Glide.with(this).load(createRequestURL(ref)).into(view)
                places[index].image = view.image
            }
            else { // Image already in object, use that
                Log.d("PIC PIC PIC", "UPLOADING PIC FOR: " + index)
                view.setImageDrawable(places[index].image)
            }
        } else { // Else, upload default image
            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_place_image))
        }
    }

    // Creates URL for Place Photo API request
    private
    fun createRequestURL(ref : String) : String {
        return  "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=1000" +
                "&photoreference=" + ref +
                "&key=" + getString(R.string.google_api_key)
    }

    // Increments index variable
    private
    fun nextIndex(n : Int) : Int{
        var i = 1
        if (lastButton == 2)
            i = 3
        lastButton = 1
        return (n + i)
    }

    // Decrements index variable
    private
    fun prevIndex(n : Int) : Int{
        var i = 1
        if (lastButton == 1)
            i = 3
        lastButton = 2
        return (n - i)
    }

    // Test dialog - ya know for testing stuff
    private
    fun testDialog(s : String) {
        // TEST DIALOG
        val builder = AlertDialog.Builder(this@ResultsActivity)
        builder.setMessage(s)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        // TEST DIALOG
    }

    // Print Place objects - again for testing
    private
    fun printPlace(place : Place) {
        testDialog("name: " + place.name +
                    "\nrating: " + place.rating +
                    "\nprice: " + place.price +
                    "\ndesc: " + place.description)
    }

} // END CLASS ResultsActivity.kt
