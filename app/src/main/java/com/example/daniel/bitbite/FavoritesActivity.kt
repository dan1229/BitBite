package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.appbar_standard.view.*
import kotlinx.android.synthetic.main.fragment_results_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class FavoritesActivity : BaseActivity(), ResultsCard.OnFragmentInteractionListener {

    /** Variables **/
    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var favoritesList: ArrayList<Place>
    var fragmentList = ArrayList<Fragment>()
    private var faveListSize = 0

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        mDrawerLayout = findViewById(R.id.favorites_drawer_layout)

        // Setup Toolbar
        toolbarBuilderNavMenu(favorites_toolbar.toolbar, "Favorites")

        // Get intent extras
        user = intent.getParcelableExtra("USER")

        // Set Nav Drawer listener
        fave_nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            navMenuSwitch(menuItem)
            true
        }

        // Set Nav Footer listener
        fave_nav_footer.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = false
            mDrawerLayout.closeDrawers()
            navMenuSwitch(menuItem)
            false
        }
    }

    /**====================================================================================================**/
    /** Updater Methods  **/

    // updateResults()
    // Populates ResultsCard fragments
    private
    fun updateCards() {
        for (i in 0 until faveListSize) {
            doAsync {
                Log.d("FAVORITES", "$i, ${favoritesList[i].name}")
                val fragment = ResultsCard.newInstance(favoritesList[i], user)
                fragmentManager.beginTransaction().add(R.id.favorites_container, fragment).commit()
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

    // updateFavorites()
    // Update Favorites list
    private
    fun updateFavorites() {
        // Get Favorites list
        val list = getFavorites(this)

        // Check if list is empty
        if(list != null) {
            // Save list and size
            favoritesList = list
            faveListSize = favoritesList.size

            // updateCards
            updateCards()
        } else {
            toast("Your favorites list is empty!")
        }
    }

    /**====================================================================================================**/
    /** Option Menu/Settings **/

    // navMenuSwitch()
    // Calls appropriate function(s) based on Nav Drawer input
    private
    fun navMenuSwitch(menuItem: MenuItem) {
        when(menuItem.toString()) {
            "Home" -> goHome()
            "Favorites" -> mDrawerLayout.closeDrawers()
            "Settings" -> goToSettings()
            "About Us" -> goToAboutUs(this)
            "Feedback" -> goToFeedback(this)
        }
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goHome()
    // Go to MainActivity.kt
    private
    fun goHome() {
        val intent = Intent(this@FavoritesActivity, MainActivity::class.java)
        startActivity(intent)
    }

    // goToSettings()
    // Go to SettingsActivity.kt
    private
    fun goToSettings() {
        val intent = Intent(this@FavoritesActivity, SettingsActivity::class.java)
        startActivity(intent)
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
        updateFavorites()
    }


}  /** END CLASS FavoritesActivity.kt **/