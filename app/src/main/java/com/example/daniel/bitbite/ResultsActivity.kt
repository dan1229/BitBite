package com.example.daniel.bitbite

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.content_results.*
import kotlinx.android.synthetic.main.fragment_results_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.util.Pair as UtilPair


class ResultsActivity : AppCompatActivity(), ResultsCard.OnFragmentInteractionListener {

    lateinit var user : MainActivity.User
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
        user = intent.getParcelableExtra("USER")
        token = intent.getStringExtra("TOKEN")
        listSize = places.size

        // Populate cards
        updateResults()

        // Set Show More button listener
        show_more_button.setOnClickListener {
            Log.d("LOAD", "start")
            startLoading(loading_results)

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
    // Populates ResultsCard fragments
    private
    fun updateResults() {
        for (i in 0..(listSize - 1)) {
            doAsync {

                val fragment = ResultsCard.newInstance(places[i], user)
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
        stopLoading(loading_results)
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
