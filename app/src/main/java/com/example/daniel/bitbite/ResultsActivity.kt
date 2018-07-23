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
    /** Duplicate Checker Methods **/

    // checkDuplicates()
    // Scans current list for duplicates
    private
    fun checkDuplicates() {
        var size = placesList.size
        var run = true
        var leftIndex = 0
        var i: Int

        while(run) {
            i = leftIndex + 1
            if(i < size) {
                val name = splitName(placesList[leftIndex].name)

                while(i < size) {
                    val temp = splitName(placesList[i].name)
                    if (name == temp) { // Names match
                        // Duplicate found - add to duplicates list and remove from placesList
                        placesList[leftIndex].duplicates.add(placesList[i])
                        placesList.removeAt(i)
                        --size
                    } else {
                        ++i
                    }
                }

                ++leftIndex
            }
            else {
                run = false
            }
        }
    }

    // splitName()
    // Gets first two words of name if available for duplicates checking
    private
    fun splitName(input: String) : String {
        var res = input

        if(input.contains(' ')) { // Contains space
           res  = res.split(' ')[0] + res.split(' ')[1]
        }

        return res
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

        // Populate cards
        updateResults()
    }

    /**====================================================================================================**/
    /** Fragment Interaction Methods **/

    // resultsCardSelected
    // Handles clicks on ResultsCard
    override fun resultsCardSelected(place: Place) {
        goToLocation(place)
    }

} /** END CLASS ResultsActivity.kt **/
