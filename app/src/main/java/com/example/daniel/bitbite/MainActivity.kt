package com.example.daniel.bitbite

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.beust.klaxon.Klaxon
import com.example.daniel.bitbite.R.style.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.appbar_standard.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.*

/** Constants **/
const val EXTRA_PLACES_LIST = "com.example.daniel.bitbite.PLACESLIST"

class MainActivity : NavActivity(), SeekBar.OnSeekBarChangeListener,
    StyleTag.OnFragmentInteractionListener {

    /** Variables **/
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    var styleTags: MutableList<StyleTag> =  mutableListOf()
    var changed = true
    var valid = false

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_seekbar_price!!.setOnSeekBarChangeListener(this)
        mDrawerLayout = findViewById(R.id.main_drawerlayout)

        // Setup Toolbar
        toolbarBuilderNavMenu(main_toolbar.toolbar, "Home")

        // Get location
        setupLocation()

        // Setup spinner
        setupAutocomplete()

        // Set up Nav Drawer
        setupNav(nav_view, nav_footer)

        /** On Click Listeners **/

        // Set on click listener for submitButton
        submitButton.setOnClickListener{
            if(placesList.isEmpty() || changed) {
                startLoading(main_loading)
                placesAsyncCall(1)
            } else {
                goToResults()
            }
        }

        // Set on click listener for I'm feeling lucky button
        feelingLuckyButton.setOnClickListener {
            // Rotate donut
            val time : Long = 1000
            rotateFast(time, main_image_luckybutton)

            if(placesList.isEmpty() || changed) {
                startLoading(main_loading)
                placesAsyncCall(2)
            } else {
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
        val styles = resources.getStringArray(R.array.style_array).asList().shuffled()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, styles)
        textView.setAdapter(adapter)

        main_autocomplete_style.setOnClickListener {
            main_autocomplete_style.showDropDown()
        }

        // On item selected listener
        main_autocomplete_style.onItemClickListener = AdapterView.OnItemClickListener{
            parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            autocompleteItemSelected(selectedItem.replace(" ", ""))
            textView.text.clear()
            closeKeyboard()
        }

        // On enter key listener
        main_autocomplete_style.setOnKeyListener { view, i, keyEvent ->
            if((i == KeyEvent.KEYCODE_ENTER) && (!textView.text.isBlank()) ) { // If input text isn't blank and ENTER is hit, get first item
                if(!adapter.isEmpty) {
                    autocompleteItemSelected(adapter.getItem(0).toString())
                    textView.text.clear()
                    closeKeyboard()
                }
            }
            true
        }
    }

    // autocompleteItemSelected()
    // Takes chosen item from autocomplete and either displays or denies it
    private
    fun autocompleteItemSelected(s : String) {
        if(styleTags.size < 3) { // Check if too many tags
            if(!containsStyleTag(s)) { // Check if duplicate tag
                changed = true
                addStyleTagFragment(s)
            } else {
                toast("Already selected $s.")
            }
        } else {
            toast("You can only select up to 3 types.")
        }
    }

    // closeKeyboard()
    // Closes soft keyboard
    private
    fun closeKeyboard() {
        val inputManager:InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

    /**====================================================================================================**/
    /** Listeners / Setup **/

    // SeekBar Listeners
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        changed = true
        user.price = progress + 1
        priceDisplay.text = priceConversion(user.price)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        main_seekbar_price.progress = 4
        user.price = 5
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        user.price = p0!!.progress + 1
    }

    // setDefaultPrice()
    // Retrieves setting for default price and sets
    private
    fun setDefaultPrice() {
        val defaultPrice = getPriceSetting(this)
        main_seekbar_price.progress = defaultPrice - 1
        user.price = defaultPrice
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToResults()
    // Go to ResultsActivity.kt, pass placesList
    private
    fun goToResults() {
        if(valid) {
            val intent = Intent(this@MainActivity, ResultsActivity::class.java)
            startActivity(intent)
        }
        stopLoading(main_loading)
    }

    // feelingLucky()
    // Go to LocationActivity.kt, call PlaceDetails API
    private
    fun feelingLucky() {
        if(valid) {
            val intent = Intent(this@MainActivity, LocationActivity::class.java)
            startActivity(intent)
        }
        stopLoading(main_loading)
    }

    // goToSettings()
    // Go to SettingsActivity.kt
    override fun goToSettings() {
        changed = true
        super.goToSettings()
    }

    // goToHome()
    // Go to MainActivity.kt
    override fun goToHome() {
        mDrawerLayout.closeDrawers()
    }


    /**====================================================================================================**/
    /** Place Search API Call **/

    // placesAsyncCall()
    // Calls Places API in Async thread and goes to another activity based on input
    private
    fun placesAsyncCall(n : Int) {
        user.style = getStyleString()

        doAsync {
            if(changed) { // If selections have changed, recall API and remake list
                placesList.clear()
                try {
                    val (x, y) = callPlacesApi(this@MainActivity, user)
                    placesList = x
                    user.token = y
                    valid = true
                } catch(e : Exception){
                    valid = false
                    uiThread {
                        stopLoading(main_loading)
                        errorAlert(e.toString())
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
                    user.lat = location.latitude
                    user.lng = location.longitude
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
    /** Geocoding API **/

    // JSON Class Representations
    class GeocodeResponse(val results:List<GeocodeResults>, val status:String)

    class GeocodeResults(val geometry:Geometry, val formatted_address:String)

    // geocodeInput()
    // Gets user input and calls calls Async thread for call
    private
    fun geocodeInput(locationEditText : EditText) {
        startLoading(main_loading)

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
                    if(response != "INVALID"){ // Confirm location if valid response
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
            user.lat = response.results[0].geometry.location.lat
            user.lng = response.results[0].geometry.location.lng
            address = response.results[0].formatted_address
        }
        return address
    }

    // geocodingUrlBuilder()
    // Creates URL for Geocoding API call
    private
    fun geocodingUrlBuilder(input : String) : String {
        return "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=$input" +
                "&key=${getApiKey()}"
    }


    /**====================================================================================================**/
    /** Dialogs **/

    // geocodeGetLocationDialog()
    // Dialog to get user location
    private
    fun geocodeGetLocationDialog() {
        stopLoading(main_loading)
        val view = layoutInflater.inflate(R.layout.dialog_location, null)
        val locationEditText = view.findViewById(R.id.locationEditText) as EditText

        // Build dialog box
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_geocode_title))
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
        stopLoading(main_loading)
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val textView = view.findViewById(R.id.confirmationTextView) as TextView
        textView.text = response

        // Build dialog box
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_geocode_title))
                .setMessage(getString(R.string.dialog_geocode_confirmation_text))
                .setCancelable(false)
                .setView(view)

        // Yes button listener
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
        }

        // No button listener
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            geocodeGetLocationDialog()
        }

        builder.show()
    }

    /**====================================================================================================**/
    /** Fragment Methods **/

    // fragmentFavoritesChanged()
    // Updates style string and removes fragments when clicked
    override fun onFragmentInteraction(frag : Fragment, string : String) {
        // Close StyleTag fragment
        fragmentManager.beginTransaction().remove(frag).commit()
        styleTags.remove(frag)
        changed = true
    }

    /**====================================================================================================**/
    /** Life Cycle Methods **/

    // onPause()
    // Handles when MainActivity.kt is paused
    override fun onPause() {
        super.onPause()
        stopLoading(main_loading)
    }

    // onResume()
    // Handles when MainActivity.kt resumes
    override fun onResume() {
        super.onResume()
        Log.d("BITBITE", "Main onResume()")
        setDefaultPrice()

        if(styleTags.size == 0) {
            addRandomStyleTag()
        }
    }

    /**====================================================================================================**/
    /** StyleTag Methods **/

    // addRandomStyleTag()
    // Adds random StyleTag fragment
    private
    fun addRandomStyleTag() {
        if(styleTags.isEmpty()) { // Check there's no style tags
            // Get appropriate style array
            val arr = getStyleArray()

            var run = true
            var s = ""
            while(run) {
                s = arr[getRandom(arr.size)]
                if(!containsStyleTag(s)) {
                    run = false
                }
            }
            autocompleteItemSelected(s)
        }
    }

    // getStyleArray()
    // Gets appropriate style array based on time
    private
    fun getStyleArray() : Array<String> {
        // Get time
        val time = Calendar.getInstance()
        val currentHour = time.get(Calendar.HOUR_OF_DAY)

        // Set default array (night)
        var arr = resources.getStringArray(R.array.styles_night_array)

        // Pick style tag based on time
        if((currentHour < 11) && (currentHour >= 5)) { // Morning
            arr = resources.getStringArray(R.array.styles_morning_array)
        }
        else if ((currentHour < 18) && (currentHour >= 12)) { // Afternoon
            arr = resources.getStringArray(R.array.styles_afternoon_array)
        }
        // else{} leave array as is (night)

        return arr
    }

    // containsStyleTag()
    // Sees if tag with passed style exists
    private
    fun containsStyleTag(input: String) : Boolean {
        for(i in 0 until styleTags.size) {
            if(styleTags[i].style == input) {
                return true
            }
        }
        return false
    }

    // getStyleString()
    // Returns style string based on present StyleTags
    private
    fun getStyleString() : String {
        var string = ""
        for(i in 0 until styleTags.size) {
            string += "${styleTags[i].style}|"
        }
        return string.removeSuffix("|")
    }

    // addStyleTag()
    // Adds style tag fragment if successfully selected from list
    private
    fun addStyleTagFragment(s : String) {
        changed = true
        val fragment = StyleTag.newInstance(s)
        styleTags.add(fragment)
        fragmentManager.beginTransaction().add(R.id.main_container_tags, fragment).commit()
    }


}  /** END CLASS MainActivity.kt **/