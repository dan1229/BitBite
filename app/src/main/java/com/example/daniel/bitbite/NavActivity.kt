package com.example.daniel.bitbite

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_nav.*

abstract class NavActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    /** Variables **/
    lateinit var mDrawerLayout: DrawerLayout


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        nav_view.setNavigationItemSelectedListener(this)
        nav_footer.setNavigationItemSelectedListener(this)

//        // Set Nav Drawer listener
//        nav_view.setNavigationItemSelectedListener { menuItem ->
//            Log.d("NAV", "nav selected listener header")
//            menuItem.isChecked = true
//            mDrawerLayout.closeDrawers()
//            navMenuSwitch(menuItem)
//            true
//        }
//
//        // Set Nav Footer listener
//        nav_footer.setNavigationItemSelectedListener { menuItem ->
//            Log.d("NAV", "nav selected listener footer")
//            menuItem.isChecked = false
//            mDrawerLayout.closeDrawers()
//            navMenuSwitch(menuItem)
//            true
//        }
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToHome()
    // Go to MainActivity.kt
    protected
    open fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // goToFavorites()
    // Go to FavoritesActivity.kt
    protected
    fun goToFavorites() {
        val intent = Intent(this, FavoritesActivity::class.java)
        intent.putExtra("USER", user)
        startActivity(intent)
    }

    // goToAboutUs()
    // Go to www.BitBite.app
    protected
    fun goToAboutUs() {
        openWebPage("https://www.BitBite.app")
    }


    // goToFeedback()
    // Go to Google Play Reviews page
    protected
    fun goToFeedback() {
        //
    }

    // goToSettings()
    // Go to SettingsActivity.kt
    protected
    open fun goToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    /**====================================================================================================**/
    /** Option Menu/Settings **/



    // onOptionsItemSelected()
    // "On click listener" for options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("NAV", "options selected")
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // navMenuSwitch()
    // Calls appropriate function(s) based on Nav Drawer input
    private
    fun navMenuSwitch(menuItem: MenuItem) {
        when(menuItem.toString()) {
            "Home" -> goToHome()
            "Favorites" -> goToFavorites()
            "Settings" -> goToSettings()
            "About Us" -> goToAboutUs()
            "Feedback" -> goToFeedback()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> goToHome()
            R.id.nav_favorites -> goToFavorites()
            R.id.nav_settings -> goToSettings()
            R.id.nav_about_us -> goToAboutUs()
            R.id.nav_feedback -> goToFeedback()
        }
        return true
    }


} /** END CLASS NavActivity.kt **/
