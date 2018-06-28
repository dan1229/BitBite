package com.example.daniel.digit

import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_location.*
import org.w3c.dom.Text

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Get place
        val place = intent.getParcelableExtra<Place>("location")

        // Set data
        updateLocation(place)

        // Set on click listener
        locationCard.setOnClickListener{
            place.openWebPage(this)
        }
    }

    // Updates information displayed
    private
    fun updateLocation(place : Place) {
        // Update Photo
        if(place.photoRef != "DEFAULT")
            placePhotoCall(place.photoRef, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))

        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationPrice).text = place.priceConversion()
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))
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
}
