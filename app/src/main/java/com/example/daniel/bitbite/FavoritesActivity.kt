package com.example.daniel.bitbite

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.appbar_standard.view.*
import kotlinx.android.synthetic.main.fragment_results_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class FavoritesActivity : NavActivity(), ResultsCard.OnFragmentInteractionListener {

    /** Variables **/
    lateinit var favoritesList: ArrayList<Place>
    var fragmentList = ArrayList<Fragment>()
    private var faveListSize = 0

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        mDrawerLayout = findViewById(R.id.favorites_drawerlayout)

        // Setup Toolbar
        toolbarBuilderNavMenu(favorites_toolbar.toolbar, "Favorites")

        // Setup Nav Drawer
        setupNav(nav_view, nav_footer)

        // Get intent extras
        user = intent.getParcelableExtra("USER")
    }

    /**====================================================================================================**/
    /** Updater Methods  **/



    // updateFavoritesSection()
    // Update Favorites Activity
    private
    fun refreshFavoritesActivity() {
        // Get Favorites list
        val list = getFavrotiesList(this)

        // Check if list is empty
        if(list != null) {
            Log.d("FAVE", "updating list")

            // Save list and size
            favoritesList = list
            faveListSize = favoritesList.size

            // updateCards
            updateCards()
        } else {
            toast("Your favorites list is empty!")
        }
    }

    // updateResults()
    // Populates ResultsCard fragments
    private
    fun updateCards() {
        Log.d("FAVE", "updating cards")

        for (i in 0 until faveListSize) {
            doAsync {
                val fragment = ResultsCard.newInstance(favoritesList[i], user)
                fragmentManager.beginTransaction().setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left)
                        .add(R.id.favorites_container, fragment).commit()
                fragmentList.add(fragment)

                uiThread {
                    if(favoritesList[i].photoRef != "DEFAULT") // Lookup image
                        favoritesList[i].placePhotoCall(this@FavoritesActivity, fragment.results_image)
                    else
                        fragment.results_image.setImageDrawable(ContextCompat.getDrawable( // Set default image
                                this@FavoritesActivity, R.drawable.default_place_image))
                }
            }
        }
    }

    /**====================================================================================================**/
    /** Options Menu Methods **/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.favorites_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.clear_list) {
            dialogConfirmClearFavorites()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when FavoritesActivity.kt is paused
    override fun onPause() {
        super.onPause()

        // Remove old cards
        for(i in 0 until fragmentList.size) {
            fragmentManager.beginTransaction().remove(fragmentList[i]).commit()
        }
        fragmentList.clear()
    }

    // onResume()
    // Handles when FavoritesActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Favorites onResume()")

        // Update Favorites list
        refreshFavoritesActivity()
    }


    /**====================================================================================================**/
    /** Fragment Interaction Methods **/

    // resultsCardSelected
    // Handles clicks on ResultsCard
    override fun resultsCardSelected(place: Place) {
        goToLocation(place)
    }

}  /** END CLASS FavoritesActivity.kt **/