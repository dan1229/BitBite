package com.example.daniel.bitbite

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.fragment_results_card.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class FavoritesActivity : AppCompatActivity(), ResultsCard.OnFragmentInteractionListener {

    /** Variables **/
    lateinit var favoritesList: ArrayList<Place>
    lateinit var user : MainActivity.User
    private lateinit var mDrawerLayout: DrawerLayout
    var listSize = 0

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        mDrawerLayout = findViewById(R.id.favorites_drawer_layout)

        // Setup Toolbar
        setSupportActionBar(toolbar_fave as Toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        // Get intent extras
        user = intent.getParcelableExtra("USER")

        // Get Favorites list
        val list = getFavorites(this)

        // Check if list is empty
        if(list != null) {
            // Save list size
            favoritesList = list
            listSize = favoritesList!!.size

            // Populate cards
            updateResults()
        } else {
            toast("Your favorites list is empty!")
        }

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
    fun updateResults() {
        for (i in 0..(listSize - 1)) {
            doAsync {

                val fragment = ResultsCard.newInstance(favoritesList[i], user)
                fragmentManager.beginTransaction().add(R.id.favorites_container, fragment).commit()

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
}
