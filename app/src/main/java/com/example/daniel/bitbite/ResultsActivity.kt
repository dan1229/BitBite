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

    var places = ArrayList<Place>()
    var listSize = 0
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        setSupportActionBar(toolbar_results)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_results.title = ""


        // Check if already instantiated
        if (savedInstanceState != null) {
            return
        }

        // Get places ArrayList
        places = intent.getParcelableArrayListExtra<Place>(EXTRA_PLACES_LIST)
        token = intent.getStringExtra("TOKEN")
        listSize = places.size

        updateResults()


        // Set Show More button listener
        show_more_button.setOnClickListener {
            doAsync {
                val (x, y) = callPlacesApi(this@ResultsActivity, token = token)
                places = x
                token = y
                listSize = places.size

                uiThread {
                    updateResults()
                }
            }
        }
    }

    /**====================================================================================================**/
    /** Updater Methods  **/

    // updateResults()
    // Updates ResultsCard fragments
    private
    fun updateResults() {
        for (i in 0..(listSize - 1)) {
            doAsync {

                val fragment = ResultsCard.newInstance(places[i])
                fragmentManager.beginTransaction().add(R.id.layout_container, fragment).commit()


                uiThread {
                    if(places[i].photoRef != "DEFAULT") // Lookup image
                        places[i].placePhotoCall(this@ResultsActivity, fragment.results_image)
                    else
                        fragment.results_image.setImageDrawable(ContextCompat.getDrawable( // Set default image
                                this@ResultsActivity, R.drawable.default_place_image))
                }
            }
        }

        updateButton()
    }

    // updateButton()
    // Updates button visibility based on token
    private
    fun updateButton() {
        if(token == "") { // Token doesn't exist
            show_more_button.visibility = View.GONE
        } else { // Token exists
            show_more_button.visibility = View.VISIBLE
        }
    }

} /** END CLASS ResultsActivity.kt **/
