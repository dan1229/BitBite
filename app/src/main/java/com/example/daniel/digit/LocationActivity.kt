package com.example.daniel.digit

import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.beust.klaxon.Klaxon
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_location.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.w3c.dom.Text
import java.net.URL

class LocationActivity : AppCompatActivity() {

    // Variables
    lateinit var place : Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Async call
        doAsync {
            // Call Place Details API
            var response = callDetailsAPI(place)

            uiThread {
                // Get place
                place = intent.getParcelableExtra<Place>("location")

                // Set data
                updateLocation(response)
            }
        }

        // Set on click listener
        locationCard.setOnClickListener{
            place.openWebPage(this)
        }
    }

    // Updates information displayed
    private
    fun updateLocation(response : DetailsResponse?) {

        // Place info updates
        if(place.photoRef != "DEFAULT")
            placePhotoCall(place.photoRef, findViewById(R.id.locationImage)) // Fetch image
        else
            findViewById<ImageView>(R.id.locationImage).setImageDrawable(ContextCompat.getDrawable( // Set default image
                    this, R.drawable.default_place_image))

        findViewById<TextView>(R.id.locationName).text = place.name
        findViewById<TextView>(R.id.locationPrice).text = place.priceConversion()
        findViewById<TextView>(R.id.locationDescription).text = place.fixDescription()
        findViewById<ImageView>(R.id.locationRating).setImageDrawable(ContextCompat.getDrawable(this, place.ratingConversion()))

        // DetailsResponse info updates

        findViewById<TextView>(R.id.locationAddress).text = response.results.formatted_address


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

    class DetailsResponse(val results:DetailsResults, val status:String)

    class DetailsResults(val formatted_address:String = "", val formatted_phone_number:String = "",
                         val reviews:List<Reviews>? = null, val website:String = "")

    class Reviews(val author_name:String = "", val text:String = "", val rating:Int)

    // Builds URL for Place Details API call
    private
    fun detailsSearchUrlBuilder() : String {
        return "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + place.placeID +
                "&key=" + getString(R.string.google_api_key)
    }

    // Calls Place Details API
    private
    fun callDetailsAPI(place : Place) : DetailsResponse? {
        var response = Klaxon().parse<DetailsResponse>(URL(detailsSearchUrlBuilder()).readText())
        return response
    }

}
