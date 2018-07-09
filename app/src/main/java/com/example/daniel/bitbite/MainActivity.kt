package com.example.daniel.bitbite

import android.Manifest
import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.Pair
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.beust.klaxon.*
import com.example.daniel.bitbite.R.style.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.seekbar_view_layout.*
import org.jetbrains.anko.*
import java.net.URL
import java.util.*
import kotlin.RuntimeException

/** Constants **/
const val EXTRA_PLACES_LIST = "com.example.daniel.bitbite.PLACESLIST"

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    /** Spinner Options **/
    val styles = arrayOf("Random", "Hispanic", "Italian", "Asian", "Health", "Breakfast", "Fast Food")
    val prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    /** Variables **/
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private val locationRequestCode = 101
    var placesList = ArrayList<Place>()
    var changed = false
    var valid = false
    var style = "Random"
    var price = 5
    var lat = 0.0
    var lng = 0.0

    /** Settings Variables **/
    var OPENNOW = true
    var RADIUS = 15 / 0.00062137
    var RANKBY = "distance"
    var DEFAULTLOCATION = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main as Toolbar)
        priceBar!!.setOnSeekBarChangeListener(this)
        changed = true

        // Get location
        setupLocation()

        // Setup spinners
        setupSpinners()

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

    // setupSpinners()
    // Setup spinners and listeners
    private
    fun setupSpinners() {
        // Adapter for styleSpinner
        styleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, styles)

        // Item selected listener for styleSpinner
        styleSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                style = styles[p2].replace(" ", "")
                changed = true
            }
        }
    }

    // SeekBar Listeners
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        changed = true
        price = progress + 1
        priceDisplay.text = price.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        priceBar.progress = 4
        price = 5
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        price = p0!!.progress + 1
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

            // Check Android version for animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                        this@MainActivity, imageView, "main_logo")
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
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
        }
    }

    // goToSettings()
    // Go to SettinsActivity.kt
    private
    fun goToSettings() {
        if(valid) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)

            // Check Android version for animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                        this@MainActivity, imageView, "main_logo")
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        }
    }


    /**====================================================================================================**/
    /** Place Search API **/

    // JSON Class Representations
    class Response(val results:List<Results>, val status:String)

    class Results(val geometry:Geometry, val name:String="Not Available", val photos:List<Photos>? = null,
                  val place_id:String="", val price_level:Int=0, val rating:Double=0.0,
                  val opening_hours:Times, val types:Array<String>)

    class Geometry(val location:LocationObj)

    class LocationObj(val lat:Double, val lng:Double)

    class Photos(val photo_reference:String="DEFAULT")

    class Times(val open_now:Boolean = true)

    // placesAsyncCall()
    // Calls Places API in Async thread and goes to another activity based on input
    private
    fun placesAsyncCall(n : Int) {
        doAsync {
            if(changed) { // If selections have changed, recall API and remake list
                placesList.clear()
                try {
                    placesList = callPlacesApi()
                    valid = true
                } catch(e : RuntimeException){
                    valid = false
                    uiThread {
                        errorAlert(e.toString().replace("java.lang.RuntimeException: ", ""))
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

    // callPlacesAPI()
    // Gets and parses JSON response from Places API
    private
    fun callPlacesApi() : ArrayList<Place> {
        val res = ArrayList<Place>()
        Log.d("STREAM", placeSearchUrlBuilder())
        val response = Klaxon().parse<Response>(URL(placeSearchUrlBuilder()).readText())
        if(response!!.status != "OK"){ // Response invalid
            if(response.status.equals("ZERO_RESULTS")) { // No result
                throw RuntimeException("No result. Please try again.")
            }
            else { // Other issue
                throw RuntimeException("Technical error. Please try again.")
            }
        }

        for(i in 0 until (response.results.size)){
            val place = convertToPlace(response.results[i])
            res.add(place)
        }

        return res
    }

    // placeSearchUrlBuilder()
    // Builds URL for PlaceSearch API Call
    private
    fun placeSearchUrlBuilder() : String {
        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
        // @Param
        // location = lat + lng
        // type = restaurant
        // *radius = dist. in m
        // *oppenow = true or false
        // *rankby = dist. or prom.
        // style = style spinner
        // price = price spinner
        // key = API key
        // * - choose in settings
        updateSettings()

        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=$lat,$lng" +
                "&type=restaurant" +
                "&maxprice=$price"

        if(OPENNOW.toString() == "true"){ // Open Now
                url += "&opennow=$OPENNOW"
        }

        if(RANKBY == "distance") { // Rank by distance
            url += "&rankby=$RANKBY"
        } else{ // Rank by prominence (use radius)
            url += "&radius=$RADIUS"
        }

        if(style != "Random"){ // Add style if not "random"
            url += "&keyword=$style"
        }

        url += "&key=${getString(R.string.google_api_key)}"

        return url
    }

    // convertToPlace()
    // Convert Response object to Place object
    private
    fun convertToPlace(results : Results) :  Place {
        val photoRef = if (results.photos != null) results.photos[0].photo_reference else "DEFAULT"
        val name = results.name
        val placeID = results.place_id
        val description = results.types[0]
        val price = results.price_level
        val rating = results.rating.toInt()
        val openNow = results.opening_hours.open_now
        val location = DoubleArray(2)
        location[0] = results.geometry.location.lat
        location[1] = results.geometry.location.lng
        return Place(name, placeID, description, photoRef, price, rating, openNow, location)
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
            doAsync {
                var response = parseGeocodeJson(text.toString())
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
    // Check permission response and react accordingly
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

    // onCreateOptionsMenu()
    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // onOptionsItemSelected()
    // "On click listener" for options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.action_settings -> { // Selected settings
                goToSettings()
            }
            R.id.action_about_us -> { // About us selected
                // Go to About activity
            }
            R.id.action_rate_us -> { // Rate us selected
                // Go to Google Play store
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // updateSettings()
    // Updates settings variables
    private
    fun updateSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        RADIUS = (prefs.getInt("radius", 15) / 0.00062137)
        OPENNOW = prefs.getBoolean("opennow", true)
        RANKBY = prefs.getString("sortby", "distance")
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
        checkboxView.setOnCheckedChangeListener { buttonView, isChecked ->
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