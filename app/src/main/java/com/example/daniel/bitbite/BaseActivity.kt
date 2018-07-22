package com.example.daniel.bitbite


/**
 * Made by Daniel Nazarian
 * 7/18/2018 @ 8:32
 */


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.appbar_standard.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    val locationRequestCode = 101

    /** Settings Variables **/
    var DEFAULTLOCATION = ""

    /** User Class Declaration **/
    @Parcelize
    data class User(var lat: Double, var lng: Double,
                    var style: String = "", var price: Int = 0,
                    var token: String = "") : Parcelable

    /** Companion Object **/
    companion object {
        var placesList = ArrayList<Place>()
        var user = User(0.0, 0.0, "", 0, "")
        var distance = ""
        var duration = ""
    }

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**====================================================================================================**/
    /** Toolbar Buidler Methods **/

    // toolbarBuilderNavMenu()
    // Builds toolbar with nav menu and label
    fun toolbarBuilderNavMenu(toolbar: Toolbar, label: String) {
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        toolbar.title = label
    }

    // toolbarBuilderUpNavLogo()
    // Builds toolbar with up nav and logo in the middle
    fun toolbarBuilderUpNavLogo(view: View) {
        setSupportActionBar(view.appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view.toolbar_logo.visibility = View.VISIBLE
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    // toolbarBuilderUpNavLabel()
    // Builders toolbar with up nav and label
    fun toolbarBuilderUpNavLabl(toolbar: Toolbar, label: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = label
    }


    /**====================================================================================================**/
    /** Intent Makers **/

    // openWebPage()
    // Opens web page to passed URL
    protected
    fun openWebPage(url : String) {
        val uris = Uri.parse(url)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        this.startActivity(intents)
    }

    // goToLocation()
    // Goes to LocationActivity.kt
    protected
    fun goToLocation(place: Place) {
        val intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("PLACE", place)
        startActivity(intent)
    }

    // goToReviews()
    // Creates Intent for Reviews.kt and animates transition
    protected
    fun goToReviews(reviews: ArrayList<Reviews>, place: Place) {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putParcelableArrayListExtra("review_list", reviews) // Pass reviews
        intent.putExtra("PLACE", place) // pass place
        startActivity(intent)
    }

    /**====================================================================================================**/
    /** Dialogs **/

    // errorAlert()
    // Generic error dialog
    fun errorAlert(input: String = getString(R.string.dialog_error_default_message)) {
        alert(input, "Uh Oh!") {
            okButton { dialog -> dialog.dismiss()  }
        }.show()
    }

    /**====================================================================================================**/
    /** Misc. **/

    // getRandom()
    // Returns an int from 0 to and excluding the passed int
    fun getRandom(n: Int) : Int {
        var res = 0
        if(n > 0) {
            res = Random().nextInt(n - 1)
        }
        return res
    }

    // getApiKey()
    // Returns API key
    fun getApiKey() : String {
        return getString(R.string.google_api_key)
    }


} /** END CLASS BaseActivity.kt **/
