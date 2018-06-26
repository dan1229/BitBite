package com.example.daniel.digit

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_results.*
import org.jetbrains.anko.toast


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

        // Get bundle
        val bundle = intent.getBundleExtra("myBundle")
        places = bundle.getParcelableArrayList<Place>(EXTRA_PLACES_LIST) as ArrayList<Place>
        listSize = places.size

        // Initial card update
        for(i in 0..2){
            updateCard(i + 1, i)
        }

        // Set on click listener for "Next 3" button
        displayNext3.setOnClickListener {
//            if ((3 * page) < listSize) { // Display next page
//                ++page
//                for (i in 0..2) { // Load next 3 cards
//                    updateCard(i + 1, 3 * page + i)
//                }
//                for (i in 0..2) { // Pre load next 3 photos
//                    if((page * 3 + 3 + i) < listSize) {
//                        val index = page * 3 + 3 + i
//                        val view = findViewById<ImageView>(R.id.invisibleView)
//                        placePhotoCall(places[index].photoRef, view, index)
//                    }
//                }
//            } else { // Can't go forward, display error
//                toast("End of list - can't go further")
//            }
            if ((3 * page) < listSize) { // Display next page
                ++page
                for (i in 0..2) {
                    //Log.d("UPDATE", "index: " + (3*page+i) + ", name: " + places[3*page+i].name)
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
                    //Log.d("UPDATE", "index: " + (3*page+i) + ", name: " + places[3*page+i].name)
                    updateCard(i + 1, 3 * page + i)
                }
            }
        }


        // Set on click listeners for cards to send to Google Maps
        card1.setOnClickListener {
            if ((3 * page) < listSize) {
                val intent = Intent(this@ResultsActivity, LocationActivity::class.java)
                intent.putExtra("location", places[3 * page])
                startActivity(intent)
            }
        }

        card2.setOnClickListener {
            if ((3 * page + 1) < listSize) {
                val intent = Intent(this@ResultsActivity, LocationActivity::class.java)
                intent.putExtra("location", places[3 * page + 1])
                startActivity(intent)
            }
        }

        card3.setOnClickListener {
            if ((3 * page + 2) < listSize) {
                val intent = Intent(this@ResultsActivity, LocationActivity::class.java)
                intent.putExtra("location", places[3 * page + 2])
                startActivity(intent)
            }
        }
    }

    // Calls update function for each segment of card
    private
    fun updateCard(card : Int, index : Int) {
        //Log.d("UPDATE", "index: " + index + ", name: " + places[index].name + ", page: $page")

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
            textView.text = places[index].priceConversion()
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_price)
        }
    }

    // Updates photo on the card
    private
    fun updatePhoto(card : Int, index : Int) {
        val view = getPhotoView(card)
        if(((index >= 0) && (index < listSize)) || (places[index].photoRef != "DEFAULT")){ // In bounds
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
            textView.text = places[index].description.capitalize().replace("_", "")
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

    // Calls Place Photo API and returns image
    private
    fun placePhotoCall(ref : String, view : ImageView, index : Int) {
        if(places[index].image == null) { // Image not already in object, download it
            Glide.with(this).load(createPhotosRequestURL(ref)).into(view)
            places[index].image = view.drawable
            Log.d("PIC", "index: $index, image: " + places[index].image)
        }
        else { // Image already in object, use that
            view.setImageDrawable(places[index].image)
        }
    }

    // Creates URL for Place Photo API request
    private
    fun createPhotosRequestURL(ref : String) : String {
        return  "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=1000" +
                "&photoreference=" + ref +
                "&key=" + getString(R.string.google_api_key)
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
