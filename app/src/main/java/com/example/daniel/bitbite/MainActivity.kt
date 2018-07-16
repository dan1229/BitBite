package com.example.daniel.bitbite

import android.Manifest
import android.app.Fragment
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.*
import com.beust.klaxon.Klaxon
import com.example.daniel.bitbite.R.style.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.net.URL

/** Constants **/
const val EXTRA_PLACES_LIST = "com.example.daniel.bitbite.PLACESLIST"

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener,
    StyleTag.OnFragmentInteractionListener {

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
    var styleTags = 0
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
        main_seekbar_price!!.setOnSeekBarChangeListener(this)
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
        setupAutocomplete()

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
                startLoading(loading_main)
                placesAsyncCall(1)
            }
            else{
                goToResults()
            }
        }

        // Set on click listener for I'm feeling lucky button
        feelingLuckyButton.setOnClickListener {
            val time : Long = 1000
            rotateFast(time, main_image_luckybutton)

            if(placesList.isEmpty() || changed) {
                startLoading(loading_main)
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

    // setupAutocomplete()
    // Setup autocomplete text view
    private
    fun setupAutocomplete() {
        val textView = findViewById<AutoCompleteTextView>(R.id.main_autocomplete_style)
        val styles = resources.getStringArray(R.array.style_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, styles)
        textView.setAdapter(adapter)

        // On item selected listener
        main_autocomplete_style.onItemClickListener = AdapterView.OnItemClickListener{
            parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            autocompleteItemSelected(selectedItem.replace(" ", ""))
            textView.text.clear()
        }

        // On enter key listener
        main_autocomplete_style.setOnKeyListener { view, i, keyEvent ->
            if((i == KeyEvent.KEYCODE_ENTER) && (!textView.text.isBlank()) ) { // If input text isn't blank and ENTER is hit, get first item
                if(!adapter.isEmpty) {
                    autocompleteItemSelected(adapter.getItem(0).toString())
                    textView.text.clear()
                }
            }
            true
        }
    }

    // autocompleteItemSelected()
    // Takes chosen item from autocomplete and either displays or denies it
    private
    fun autocompleteItemSelected(s : String) {
        if(styleTags < 3) {
            // Add to style string
            if(style == "Random"){
                style = s
            } else {
                style += "|$s"
            }

            ++styleTags
            changed = true
            addStyleTagFragment(s)
        } else {
            toast("You can only select up to 3 types.")
        }
    }

    // addStyleTag()
    // Adds style tag fragment if successfully selected from list
    private
    fun addStyleTagFragment(s : String) {
        val fragment = StyleTag.newInstance(s)
        fragmentManager.beginTransaction().add(R.id.main_container_tags, fragment).commit()
    }

    // SeekBar Listeners
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        changed = true
        price = progress + 1
        priceDisplay.text = priceConversion(price)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        main_seekbar_price.progress = 4
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
        main_seekbar_price.progress = defaultPrice
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
        }
        stopLoading(loading_main)
    }

    // feelingLucky()
    // Go to LocationActivity.kt, call PlaceDetails API
    private
    fun feelingLucky() {
        if(valid) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java)
            intent.putExtra("PLACE", placesList[0])
            intent.putExtra("USER", user)
            startActivity(intent)
        }
        stopLoading(loading_main)
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

        doAsync {
            if(changed) { // If selections have changed, recall API and remake list
                placesList.clear()
                try {
                    val (x, y) = callPlacesApi(this@MainActivity, user)
                    placesList = x
                    next_page_token = y
                    valid = true
                } catch(e : Exception){
                    valid = false
                    uiThread {
                        stopLoading(loading_main)
                        errorAlert()
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
        startLoading(loading_main)

        // Get text, if valid input pass to
        val text = locationEditText.text
        var isValid = true
        if (text.isBlank()) { // Blank submission - try again
            locationEditText.error = "Location"
            isValid = false
            geocodeGetLocationDialog()
        }

        if(isValid) { // Valid submission - call API in another thread
            doAsync {
                val response = parseGeocodeJson(text.toString())
                uiThread {
                    if(response != "INVALID"){ // Confirm locatoin if valid response
                        confirmLocationDialog(response)
                    }
                    else{ // Prompt for location again if invalid
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
            }
        }
    }


    /**====================================================================================================**/
    /** Option Menu/Settings **/

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
        stopLoading(loading_main)
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
        stopLoading(loading_main)
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val textView = view.findViewById(R.id.confirmationTextView) as TextView
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
        }

        // No button listener
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            geocodeGetLocationDialog()
        }

        builder.show()
    }

    // errorAlert()
    // Generic error dialog
    fun errorAlert(input: String = getString(R.string.default_technical_errors)) {
        alert(input, "Uh Oh!") {
            okButton { dialog -> dialog.dismiss()  }
        }.show()
    }


    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Updates style string and removes fragments when clicked
    override fun onFragmentInteraction(frag : Fragment, string : String) {

        // Update style string
        val index = style.indexOf(string)
        style = style.removeRange(index, (index + string.length))
        changed = true

        if ((index > 0) && (style[index - 1] == '|')) {
            style = style.removeRange(index - 1, index)
        }
        else if ((index < style.length) && (style[index] == '|')) {
            style = style.removeRange(index , index + 1)
        }

        // Close fragment
        fragmentManager.beginTransaction().remove(frag).commit()
        --styleTags
    }


}  /** END CLASS MainActivity.kt **/