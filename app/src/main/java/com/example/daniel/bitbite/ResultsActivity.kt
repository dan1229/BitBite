package com.example.daniel.bitbite

import android.app.Fragment
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
    var fragmentList = ArrayList<Fragment>()
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


        // Set Show More button listener
        show_more_button.setOnClickListener {
            Log.d("LOAD", "start")
            startLoading(loading_results)

            doAsync {
                val (x, y) = callPlacesApi(this@ResultsActivity, token = token)
                places = x
                token = y
                listSize = places.size
            }
            updateResults()
        }
    }

    /**====================================================================================================**/
    /** Updater Methods  **/

    // updateResults()
    // Populates ResultsCard fragments
    private
    fun updateResults() {
        for (i in 0 until listSize) {
            doAsync {
                val fragment = ResultsCard.newInstance(places[i], user)
                fragmentManager.beginTransaction().add(R.id.layout_container, fragment).commit()
                fragmentList.add(fragment)

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

    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when ResultsActivity.kt is paused
    override fun onPause() {
        super.onPause()

        // Remove old cards
        for(i in 0 until fragmentList.size) {
            fragmentManager.beginTransaction().remove(fragmentList[i]).commit()
        }
        fragmentList.clear()
    }

    // onResume()
    // Handles when ResultsActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Results onResume()")

        // Populate cards
        updateResults()
    }

} /** END CLASS ResultsActivity.kt **/
