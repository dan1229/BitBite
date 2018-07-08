package com.example.daniel.bitbite

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.beust.klaxon.Klaxon
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_results.*
import org.jetbrains.anko.toast
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.content_results.*
import kotlinx.android.synthetic.main.content_results.view.*
import kotlinx.android.synthetic.main.fragment_results_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.w3c.dom.Text
import java.net.URL
import android.util.Pair as UtilPair


class ResultsActivity : AppCompatActivity(), ResultsCard.OnFragmentInteractionListener {

    /** Variables **/
    var places = ArrayList<Place>()
    var listSize = 0
    var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        setSupportActionBar(toolbar_results)
        toolbar_results.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check if already instantiated
        if (savedInstanceState != null) {
            return
        }

        // Get places ArrayList
        val places = intent.getParcelableArrayListExtra<Place>(EXTRA_PLACES_LIST)
        listSize = places.size

        doAsync {
            for(i in 0..(listSize - 1)) {
                val fragment = ResultsCard.newInstance(places[i])
                fragmentManager.beginTransaction().add(R.id.layout_container, fragment).commit()

                uiThread {
                    places[i].placePhotoCall(this@ResultsActivity, fragment.results_image)
                }
        }



//            doAsync {
//                val fragment = ResultsCard.newInstance(places[i])
//                places[i].placePhotoCall(this@ResultsActivity, fragment.results_image)
//
//                uiThread {
//                    fragmentManager.beginTransaction().add(R.id.layout_container, fragment).commit()
//                }
//            }
        }

//        // Initial card update
//        for(i in 0..2){
//            updateCard(i + 1, i)
//        }
//
//        // Set on click listener for "Next 3" button
//        displayNext3.setOnClickListener {
//            if ((3 * (page + 1)) < listSize) { // Display next page
//                ++page
//                for (i in 0..2) {
//                    updateCard(i + 1, 3 * page + i)
//                }
//            }
//            else { // Can't go forward, display error
//                toast("End of list - can't go further")
//            }
//        }
//
//        // Set on click listener for "Go back" button
//        displayPrev3.setOnClickListener {
//            if (page == 0) { // Can't go back, display error
//                toast("Beginning of list - can't go back further")
//            }
//            else { // Display prev page
//                --page
//                for (i in 0..2) {
//                    updateCard(i + 1, 3 * page + i)
//                }
//            }
//        }
//
//
//        // Set on click listeners for cards -> LocationActivity.kt
//        card1.setOnClickListener {
//            if ((3 * page) < listSize)
//                goToLocation(places[3 * page], card1, image1, name1)
//        }
//
//        card2.setOnClickListener {
//            if ((3 * page + 1) < listSize)
//                goToLocation(places[3 * page + 1], card2, image2, name2)
//        }
//
//        card3.setOnClickListener {
//            if ((3 * page + 2) < listSize)
//                goToLocation(places[3 * page + 2], card3, image3, name3)
//        }
    }

    override fun onFragInteraction(uri: Uri) {
        super.onFragInteraction(uri)
    }

    /**====================================================================================================**/
    /** Intent Makers **/

//    // goToLocation()
//    // Goes to LocationActivity, calls Place Details API
//    fun goToLocation(place : Place, card : ResultsCard) {
//
//        // Create Intent
//        val intent = Intent(this@ResultsActivity, LocationActivity::class.java)
//        intent.putExtra("place", place)
//
//        // Check Android version for animation
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val options = ActivityOptions.makeSceneTransitionAnimation(this@ResultsActivity,
//                    UtilPair.create<View, String>(card.card, "card"),
//                    UtilPair.create<View, String>(card.image, "place_image"),
//                    UtilPair.create<View, String>(card.name, "place_name"))
//            startActivity(intent, options.toBundle())
//        } else {
//            startActivity(intent)
//        }
//    }


    /**====================================================================================================**/
    /** Updater Methods **/
//
//    // updateCard()
//    // Calls update function for each segment of card
//    private
//    fun updateCard(card : Int, index : Int) {
//        var i = index
//        if(i >= listSize) { // Check if index is in bounds
//            i = -1
//        }
//        updateName(card, i)
//        updatePrice(card, i)
//        updatePhoto(card, i)
//        updateRating(card, i)
//        updateDescription(card, i)
//    }
//
//    // updateName()
//    private
//    fun updateName(card : Int, index : Int) {
//        val textView = getNameView(card)
//        if(index >= 0){ // In bounds
//            textView.text = ellipsizeText(places[index].name, 25)
//        } else{ // Out of bounds, default val
//            textView.setText(R.string.default_name)
//        }
//    }
//
//    // updatePrice()
//    private
//    fun updatePrice(card : Int, index : Int) {
//        val textView = getPriceView(card)
//        if(index >= 0){ // In bounds
//            textView.text = places[index].priceConversion()
//        } else{ // Out of bounds, default val
//            textView.setText(R.string.default_price)
//        }
//    }
//
//    // updatePhoto()
//    private
//    fun updatePhoto(card : Int, index : Int) {
//        val view = getPhotoView(card)
//        if((index >= 0) && (places[index].photoRef != "DEFAULT")){ // In bounds
//            places[index].placePhotoCall(this, view)
//        } else{ // Out of bounds, default photo
//            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_place_image))
//        }
//    }
//
//    // updateRating()
//    private
//    fun updateRating(card : Int, index : Int) {
//        val view = getRatingView(card)
//        if(index >= 0){ // In bounds
//            view.setImageDrawable(ContextCompat.getDrawable(this, places[index].ratingConversion()))
//        } else{ // Out of bounds, default photo
//            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_star))
//        }
//    }
//
//    // updateDescription()
//    private
//    fun updateDescription(card : Int, index : Int) {
//        val textView = getDescriptionView(card)
//        if(index >= 0){ // In bounds
//            textView.text = places[index].fixDescription()
//        } else{ // Out of bounds, default val
//            textView.setText(R.string.default_description)
//        }
//    }


    /**====================================================================================================**/
    /** Find View Methods **/
//
//    // getNameView()
//    // Gets TextView for name view based on card number
//    private
//    fun getNameView(card : Int) = when(card) {
//        1 -> findViewById(R.id.name1)
//        2 -> findViewById(R.id.name2)
//        3 -> findViewById(R.id.name3)
//        else -> findViewById<TextView>(R.id.name1)
//    }
//
//    // getPriceView()
//    // Gets TextView for price view based on card number
//    private
//    fun getPriceView(card : Int) = when(card) {
//        1 -> findViewById(R.id.price1)
//        2 -> findViewById(R.id.price2)
//        3 -> findViewById(R.id.price3)
//        else -> findViewById<TextView>(R.id.price1)
//    }
//
//    // getPhotoView()
//    // Gets ImageView for photo view based on card number
//    private
//    fun getPhotoView(card : Int) = when(card) {
//        1 -> findViewById(R.id.image1)
//        2 -> findViewById(R.id.image2)
//        3 -> findViewById(R.id.image3)
//        else -> findViewById<ImageView>(R.id.image1)
//    }
//
//    // getRatingView()
//    // Gets TextView for rating view based on card number
//    private
//    fun getRatingView(card : Int) = when(card) {
//        1 -> findViewById(R.id.rating1)
//        2 -> findViewById(R.id.rating2)
//        3 -> findViewById(R.id.rating3)
//        else -> findViewById<ImageView>(R.id.rating1)
//    }
//
//    // getDescriptionView()
//    // Gets TextView for description view based on card number
//    private
//    fun getDescriptionView(card : Int) = when(card) {
//        1 -> findViewById(R.id.description1)
//        2 -> findViewById(R.id.description2)
//        3 -> findViewById(R.id.description3)
//        else -> findViewById<TextView>(R.id.description1)
//    }


} /** END CLASS ResultsActivity.kt **/
