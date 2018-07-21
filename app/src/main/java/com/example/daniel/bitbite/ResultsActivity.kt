package com.example.daniel.bitbite

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.content_results.*
import org.jetbrains.anko.doAsync
import android.util.Pair as UtilPair


class ResultsActivity : BaseActivity(), ResultsCard.OnFragmentInteractionListener {

    var places = ArrayList<Place>()
    var fragmentList = ArrayList<ResultsCard>()
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Setup Toolbar
        toolbarBuilderUpNavLogo(results_toolbar)

        // Check if already instantiated
        if (savedInstanceState != null) {
            return
        }

        // Get places ArrayList
        places = intent.getParcelableArrayListExtra<Place>(EXTRA_PLACES_LIST)
        user = intent.getParcelableExtra("USER")
        token = intent.getStringExtra("TOKEN")


        // Set Show More button listener
        show_more_button.setOnClickListener {
            startLoading(loading_results)

            doAsync {
                places.clear()
                val (x, y) = callPlacesApi(this@ResultsActivity, token = token)
                places = x
                token = y
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
        // Create fragments and add to layout
        for (i in 0 until places.size) {
            // Make fragment and add to layout
            val fragment = ResultsCard.newInstance(places[i], user)
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .add(R.id.layout_container, fragment).commit()
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
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .remove(fragmentList[i]).commit()
        }
        fragmentList.clear()
    }

    // onResume()
    // Handles when ResultsActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Results onResume()")
    }

    // onPostResume()
    // Handles post resume
    override fun onPostResume() {
        // Populate cards
        updateResults()

        super.onPostResume()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    override fun onFragInteraction() {
        show_more_button.visibility = View.GONE
    }

} /** END CLASS ResultsActivity.kt **/
