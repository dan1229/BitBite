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
        var listSize = places.size

        for (i in 0..(listSize - 1)) {
            doAsync {

                val fragment = ResultsCard.newInstance(places[i])
                fragmentManager.beginTransaction().add(R.id.layout_container, fragment).commit()


                uiThread {
                    places[i].placePhotoCall(this@ResultsActivity, fragment.results_image)
                }
            }
        }
    }

} /** END CLASS ResultsActivity.kt **/
