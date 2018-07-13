package com.example.daniel.bitbite

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.beust.klaxon.*
import com.example.daniel.bitbite.R.style.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading_screen.*
import org.jetbrains.anko.*
import java.net.URL
import kotlin.RuntimeException
import kotlin.collections.ArrayList

/** Constants **/
const val EXTRA_PLACES_LIST = "com.example.daniel.bitbite.PLACESLIST"

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    /** Spinner Options **/
    val styles = arrayOf("Random", "Hispanic", "Italian", "Asian", "Health", "Breakfast", "Fast Food")

    /** Variables **/
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var mDrawerLayout: DrawerLayout
    private val locationRequestCode = 101
    private lateinit var user: User
    var placesList = ArrayList<Place>()
    var next_page_token = ""
    var changed = false
    var valid = false
    var style = "Random"
    var price = 5
    var lat = 0.0
    var lng = 0.0

    /** Settings Variables **/
    var DEFAULTLOCATION = ""

    /** User Class Declaration **/
    @Parcelize
    data class User(var lat: Double, var lng: Double,
                    var style: String = "", var price: Int = 0) : Parcelable


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        priceBar!!.setOnSeekBarChangeListener(this)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        changed = true

        // Setup toolbar
        setSupportActionBar(toolbar_main as Toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        // Get location
        setupLocation()

        // Set default price
        setDefaultPrice()

        // Setup spinner
        setupSpinner()

        // Set Nav Drawer listener
        nav_view.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            navMenuSwitch(menuItem)
            true
        }

        // Set Nav Footer listener
        nav_footer.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = false
            mDrawerLayout.closeDrawers()
            navMenuSwitch(menuItem)
            false
        }

        // Set on click listener for submitButton
        submitButton.setOnClickListener{
            if(placesList.isEmpty() || changed) {
                placesAsyncCall(1)
            }
            else{
                goToResults()
            }
        }

        // Set on click listener for I'm feeling lucky button
        feelingLuckyButton.setOnClickListener {
            if(placesList.isEmpty() || changed) {
                placesAsyncCall(2)
            }
            else{
                feelingLucky()
            }
        }
    }


    /**====================================================================================================**/
    /** Setup **/

    // setupLocation()
    // Checks default location exists, else setup location
    private
    fun setupLocation() {
        if(DEFAULTLOCATION != "") { // Default exists
            doAsync{
                parseGeocodeJson(DEFAULTLOCATION)
            }
        } else { // Request permission or get location from user
            setupPermissions()
        }
    }

    // setupSpinner()
    // Setup spinners and listeners
    private
    fun setupSpinner() {
        // Adapter for styleSpinner
        styleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, styles)

        // Item selected listener for styleSpinner
        styleSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                style = styles[p2].replace(" ", "")
                styleDisplay.text = styles[p2]
                changed = true
            }
        }
    }

    // SeekBar Listeners
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        changed = true
        price = progress + 1
        priceDisplay.text = priceConversion(price)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        priceBar.progress = 4
        price = 5
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        price = p0!!.progress + 1
        user.price = price
    }

    // setDefaultPrice()
    // Retrieves setting for default price and sets
    private
    fun setDefaultPrice() {
        val defaultPrice = getPriceSetting(this)
        priceBar.progress = defaultPrice
        price = defaultPrice
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToResults()
    // Go to ResultsActivity.kt, pass placesList
    private
    fun goToResults() {
        if(valid) {
            val intent = Intent(this@MainActivity, ResultsActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_PLACES_LIST, placesList)
            intent.putExtra("TOKEN", next_page_token)
            intent.putExtra("USER", user)
            startActivity(intent)
            loadingScreen(loading_main)
        }
    }

    // feelingLucky()
    // Go to LocationActivity.kt, call PlaceDetails API
    private
    fun feelingLucky() {
        if(valid) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java)
            intent.putExtra("place", placesList[0])
            startActivity(intent)
            loadingScreen(loading_main)
        }
    }


    /** NAV BAR INTENT MAKERS **/

    // goToFavorites()
    // Go to FavoritesActivity.kt
    private
    fun goToFavorites() {
        val intent = Intent(this@MainActivity, FavoritesActivity::class.java)
        intent.putExtra("USER", user)
        startActivity(intent)
    }

    // goToSettings()
    // Go to SettingsActivity.kt
    private
    fun goToSettings() {
        changed = true
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }


    /**====================================================================================================**/
    /** Place Search API Call **/

    // placesAsyncCall()
    // Calls Places API in Async thread and goes to another activity based on input
    private
    fun placesAsyncCall(n : Int) {
        // Update user variable
        user = User(lat, lng, style, price)
        loadingScreen(loading_main)

        doAsync {
            if(changed) { // If selections have changed, recall API and remake list
                placesList.clear()
                try {
                    val (x, y) = callPlacesApi(this@MainActivity, user)
                    placesList = x
                    next_page_token = y
                    valid = true
                } catch(e : RuntimeException){
                    valid = false
                    uiThread {
                        errorAlert("Technical Error. Please try again.")
                    }
                }
                changed = false
            }
            uiThread {
                if(n == 1) { // Submit button - go to ResultsActivity
                    goToResults()
                }
                else if (n == 2) { // Feeling lucky button - go to LocationActivity
                    feelingLucky()
                }
            }
        }
    }

    /**====================================================================================================**/
    /** Geocoding API **/

    // JSON Class Representations
    class GeocodeResponse(val results:List<GeocodeResults>, val status:String)

    class GeocodeResults(val geometry:Geometry, val formatted_address:String)

    // geocodeInput()
    // Gets user input and calls calls Async thread for call
    private
    fun geocodeInput(locationEditText : EditText) {
        val text = locationEditText.text
        var isValid = true
        if (text.isBlank()) { // Blank submission - try again
            locationEditText.error = "Location"
            isValid = false
            geocodeGetLocationDialog()
        }
        if(isValid) { // Valid submission - call API in another thread
            loadingScreen(loading_main)
            doAsync {
                val response = parseGeocodeJson(text.toString())
                uiThread {
                    if(response != "INVALID"){
                        confirmLocationDialog(response)
                    }
                    else{
                        toast("Invalid input. Please try again.")
                        geocodeGetLocationDialog()
                    }
                }
            }
        }
    }

    // parseGeocodeJson()
    // Calls geocoding API and parses JSON
    private
    fun parseGeocodeJson(input : String) : String {
        val address:String
        val response = Klaxon().parse<GeocodeResponse>(URL(geocodingUrlBuilder(input)).readText())
        if (response!!.status != "OK") {
            address = "INVALID"
        } else {
            lat = response.results[0].geometry.location.lat
            lng = response.results[0].geometry.location.lng
            address = response.results[0].formatted_address
        }
        return address
    }

    // geocodingUrlBuilder()
    // Creates URL for Geocoding API call
    private
    fun geocodingUrlBuilder(input : String) : String {
        return "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + input +
                "&key=" + getString(R.string.google_api_key)
    }


    /**====================================================================================================**/
    /** Location Permission **/

    // setupPermissions()
    // Check if location permission is granted already, else make request
    private
    fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, // Check if permission is granted
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) { // PERMISSION ALREADY GRANTED - use sensors to get lat/lng
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location == null) {
                    geocodeGetLocationDialog()
                }
                else {
                    lat = location.latitude
                    lng = location.longitude
                    user = User(lat, lng)
                }
            }
        } else { // PERMISSION NOT YET ASKED - prompt user for permission
            makeRequest()
        }
    }

    // makeRequest()
    // Make request for location permission
    private
    fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    // onRequestPermissionResult()
    // Check permission detailsResponse and react accordingly
    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            locationRequestCode -> {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) { // PERMISSION GRANTED - use sensors to get lat/lng
                    setupPermissions()
                } else { // PERMISSION DENIED - prompt user for location
                    geocodeGetLocationDialog()
                }
                return
            }
        }
    }


    /**====================================================================================================**/
    /** Option Menu/Settings **/

    // updateDefaultLocation()
    // Updates default location (called in confirmationDialog)
    private
    fun updateDefaultLocation(input : String) {
        Log.d("LOCATION", "updating default with $input")
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString(DEFAULTLOCATION, input)
        editor.apply()
        Log.d("LOCATION", "default location is " + getString(R.string.default_location))
    }

    // onOptionsItemSelected()
    // "On click listener" for options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
            "Home" -> mDrawerLayout.closeDrawers()
            "Favorites" -> goToFavorites()
            "Settings" -> goToSettings()
            "About Us" -> goToAboutUs(this)
            "Feedback" -> goToFeedback(this)
        }
    }


    /**====================================================================================================**/
    /** Dialogs **/

    // geocodeGetLocationDialog()
    // Dialog to get user location
    private
    fun geocodeGetLocationDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_location, null)
        val locationEditText = view.findViewById(R.id.locationEditText) as EditText

        // Build dialog box
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.location_dialog))
                .setMessage("Please enter your location")
                .setCancelable(false)
                .setView(view)

        // Okay button listener
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            geocodeInput(locationEditText)
            dialog.dismiss()
        }

        // Enter key listener
        builder.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                geocodeInput(locationEditText)
                dialog.dismiss()
                return@OnKeyListener true
            }
            false
        })

        builder.show()
    }

    // confirmationDialog()
    // Dialog to confirm user location
    private
    fun confirmLocationDialog(response : String) {
        loadingScreen(loading_main)

        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val textView = view.findViewById(R.id.confirmationTextView) as TextView
        var checkbox = false
        textView.text = response

        // Build dialog box
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.location_dialog))
                .setMessage(getString(R.string.confirmation_text))
                .setCancelable(false)
                .setView(view)

        // Yes button listener
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            user = User(lat, lng)
            if(checkbox) { // If default location checkbox is checked, update settings
                updateDefaultLocation(response)
            }
        }

        // No button listener
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            geocodeGetLocationDialog()
        }

        // Check box listener
        val checkboxView = view.findViewById(R.id.confirmationCheckbox) as CheckBox
        checkboxView.setOnCheckedChangeListener { _, isChecked ->
            checkbox = isChecked
        }

        builder.show()
    }

    // errorAlert()
    // Generic error dialog
    private
    fun errorAlert(input : String) {
        alert(input, "Uh Oh!") {
            okButton { dialog -> dialog.dismiss()  }
        }.show()
    }

}  /** END CLASS MainActivity.kt **/