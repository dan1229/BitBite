package com.example.daniel.digit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
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
        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationPrice).text = place.priceConversion()
        findViewById<ImageView>(R.id.locationImage).setImageDrawable(place.image)
        findViewById<TextView>(R.id.locationDescription).text = place.description.capitalize().replace("_", "")
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))
    }
}
