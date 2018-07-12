package com.example.daniel.bitbite

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_main.*

class FavoritesActivity : AppCompatActivity() {

    /** Variables **/
    lateinit var user : MainActivity.User
    private lateinit var mDrawerLayout: DrawerLayout

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
