package com.example.daniel.bitbite

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.content_results.*
import org.jetbrains.anko.doAsync
import android.util.Pair as UtilPair


class ResultsActivity : BaseActivity(), ResultsCard.OnFragmentInteractionListener {

    var fragmentList = ArrayList<ResultsCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Setup Toolbar
        toolbarBuilderUpNavLogo(results_toolbar)

        // Check if already instantiated
        if (savedInstanceState != null) {
            return
        }

        // Check for duplicates
        checkDuplicates()

        // Create ResultsCard Fragments
        createResultsCardFragments()

        // Set Show More button listener
        show_more_button.setOnClickListener {
            startLoading(loading_results)

            doAsync {
                placesList.clear()
                val (x, y) = callPlacesApi(this@ResultsActivity, token = user.token)
                placesList = x
                user.token = y

            }
            // Check for duplicates
            checkDuplicates()

            // Create fragments
            createResultsCardFragments()

            // Populate Activity
            updateResults()
        }
    }

    /**====================================================================================================**/
    /** Updater Methods  **/

    // createResultsCardFragments()
    // Creates Results Card fragments and handles fragmentList
    private
    fun createResultsCardFragments() {
        fragmentList.clear()

        for(i in 0 until placesList.size) {
            val fragment = ResultsCard.newInstance(placesList[i], user)
            fragment.index = i
            fragmentList.add(i, fragment)
        }
    }

    // updateResults()
    // Populates ResultsCard fragments
    private
    fun updateResults() {
        // Add fragment to layout
        for (i in 0 until placesList.size) {
            fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                    .add(R.id.layout_container, fragmentList[i]).commit()
        }

        stopLoading(loading_results)
        updateButton()
    }

    // removeResults()
    // Removes all ResultsCard fragments
    private
    fun removeResults() {
        for(i in 0 until fragmentList.size) {
            fragmentManager.beginTransaction().remove(fragmentList[i]).commit()
        }
    }

    // updateButton()
    // Updates button visibility based on token
    private
    fun updateButton() {
        if(user.token == "") { // Token doesn't exist
            show_more_button.visibility = View.GONE
        } else { // Token exists
            show_more_button.visibility = View.VISIBLE
        }
    }


    /**====================================================================================================**/
    /** Duplicate Methods **/

    // checkDuplicates()
    // Scans current list for duplicates
    private
    fun checkDuplicates() {
        var size = placesList.size

        for(i in 0 until size) {
            for(j in (i + 1) until size) {
                if (j < size) {
                    if (placesList[i].name == placesList[j].name) { // Names match
                        Log.d("DUPLICATES", "match: ${placesList[j].name}, i: $j")

                        placesList[i].duplicates.add(placesList[j]) // Add to duplicates
                        placesList.removeAt(j) // Remove duplicate from placesList
                        --size
                    }
                }
            }
        }
    }

    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when ResultsActivity.kt is paused
    override fun onPause() {
        super.onPause()

        // Remove old cards
        removeResults()
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
        super.onPostResume()

        // Populate cards
        updateResults()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    // resultsCardSelected
    // Handles clicks on ResultsCard
    override fun resultsCardSelected(place: Place) {
        goToLocation(place)
    }

} /** END CLASS ResultsActivity.kt **/
