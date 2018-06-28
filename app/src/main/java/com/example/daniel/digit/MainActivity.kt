package com.example.daniel.digit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.beust.klaxon.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.net.URL
import java.util.*
import kotlin.RuntimeException

// Constants
const val EXTRA_PLACES_LIST = "com.example.daniel.digit.PLACESLIST"

class MainActivity : AppCompatActivity() {

    // Spinner options
    val styles = arrayOf("Random", "Hispanic", "Italian", "Asian", "Health", "Breakfast", "Fast Food")
    val prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    // Variables
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private val locationRequestCode = 101
    var placesList = ArrayList<Place>()
    var style = "Random"
    var changed = false
    var price = -1
    var lat = 0.0
    var lng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar)

        // Setup spinners
        spinnerSetup()

        // Request location permission
        setupPermissions()

        //set on click listener for submitButton
        submitButton.setOnClickListener{
            try{
                placesAsyncCall(1)
            } catch (e : RuntimeException) {
                errorAlert(e)
            }
        }

        //set on click listener for I'm feeling lucky button
        feelingLuckyButton.setOnClickListener {
            if(placesList.isEmpty()) {
                try {
                    placesAsyncCall(2)
                } catch (e : RuntimeException){
                    errorAlert(e)
                }
            }
            else{
                feelingLucky()
            }
        }
    }

    // Creates URL for Place Search API call
    private
    fun searchUrlBuilder() : String {
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

        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + lat + "," + lng +
                "&type=restaurant" +
                "&radius=25000" +
                "&opennow=true" +
                "&rankby=distance"
        if(!style.equals("Random")){ // Random restaurant not selected
            url += "&keyword=" + style
        }
        if(price != 0){ // User did not select any price
            url += "&maxprice=" + price
        }
        url += "&key=" + getString(R.string.google_api_key)

        return url
    }

    // Creates URL for Geocoding API call
    private
    fun geocodingUrlBuilder(input : String) : String {
        return "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + input +
                "&key=" + getString(R.string.google_api_key)
    }

    class geocodeResponse(val results:List<geocodeResults>, val status:String)

    class geocodeResults(val geometry:Geometry, val formatted_address:String)


    // Calls Places API in Async thread and goes to another activity based on input
    private
    fun placesAsyncCall(n : Int) {
        doAsync {
            if(changed) { // If selections have changed, recall API and remake list
                placesList.clear()
                try {
                    placesList = streamJSON()
                    changed = false
                } catch (e: java.lang.RuntimeException) {
                    throw (e)
                }
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

    // Go to ResultsActivity.kt, pass placesList
    private
    fun goToResults() {
        val intent = Intent(this@MainActivity, ResultsActivity::class.java)
        var bundle = Bundle()
        bundle.putParcelableArrayList(EXTRA_PLACES_LIST, placesList)
        intent.putExtra("myBundle",bundle)
        startActivity(intent)
    }

    // Go to LocationActivity.kt, pass placesList[0]
    private
    fun feelingLucky() {
        val intent = Intent(this@MainActivity, LocationActivity::class.java)
        intent.putExtra("location", placesList[0])
        startActivity(intent)
    }

    private
    fun errorAlert(e : RuntimeException) {
        alert(e.toString() + ". Please try again.") {
            title = "Uh Oh!"
        }.show()
    }

    class Response(val results:List<Results>, val status:String)

    class Results(val geometry:Geometry, val name:String="Not Available", val photos:List<Photos>? = null, val place_id:String="",
                  val price_level:Int=0, val rating:Double=0.0, val types:Array<String>)

    class Geometry(val location:LocationObj)

    class LocationObj(val lat:Double, val lng:Double)

    class Photos(val photo_reference:String="DEFAULT")

    // Streams and parses JSON response from Places API
    private
    fun streamJSON() : ArrayList<Place> {
        var res = ArrayList<Place>()
        Log.d("STREAM", searchUrlBuilder())
        var response = Klaxon().parse<Response>(URL(searchUrlBuilder()).readText())
        if(response!!.status != "OK"){
            if((response.status == "ZERO_RESULTS") || (response.results.isEmpty())) {
                throw RuntimeException("No Results")
            }
            else {
                throw RuntimeException("Invalid Request")
            }
        }

        for(i in 0 until (response.results.size)){
            var place = convertToPlace(response.results[i])
            res.add(place)
        }

        return res
    }

    // Convert Response object to Place
    private
    fun convertToPlace(results : Results) :  Place {
        val photoRef = if (results.photos != null) results.photos[0].photo_reference else "DEFAULT"
        val name = results.name
        val placeID = results.place_id
        val description = results.types[0]
        val price = results.price_level
        val rating = results.rating.toInt()
        val location = DoubleArray(2)
        location[0] = results.geometry.location.lat
        location[1] = results.geometry.location.lng
        return Place(name, placeID, description, photoRef, price, rating, location)
    }

    // Check if location permission is granted already
    private
    fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, // Check if permission is granted
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) { // PERMISSION GRANTED - use sensors to get lat/lng
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                lat = location.latitude
                lng = location.longitude
            }
        } else { // PERMISSION DENIED - prompt user for location
            makeRequest()
        }
    }

    // Make request for location permission
    private
    fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    // Check permission response
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

    // Parse geocode JSON response
    private
    fun parseGeocodeJson(input : String) : String {
        var address:String
        var response = Klaxon().parse<geocodeResponse>(URL(geocodingUrlBuilder(input)).readText())
        if (response!!.status != "OK") {
            address = "INVALID"
        } else {
            lat = response.results[0].geometry.location.lat
            lng = response.results[0].geometry.location.lng
            address = response.results[0].formatted_address
        }
        return address
    }

    // Alert dialog for entering location
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

        builder.show()
    }

    // Goes through Async call for Geocoding API and checks response
    private
    fun geocodeInput(locationEditText : EditText) {
        val text = locationEditText.text
        var isValid = true
        if (text.isBlank()) {
            locationEditText.error = "Location"
            isValid = false
        }
        // Call API in another thread
        if(isValid) {
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

    // Confirms location returned from geocodeGetLocationDialog
    private
    fun confirmLocationDialog(response : String) {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val textView = view.findViewById(R.id.confirmationTextView) as TextView
        textView.text = response

        // Build dialog box
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.location_dialog))
                .setMessage("@string/confirmation_message")
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

    // Setup spinners and listeners in MainActivity
    private
    fun spinnerSetup(){
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

        //Adapter for priceSpinner
        priceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prices)

        //item selected listener for priceSpinner
        priceSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                price = p2
                changed = true
            }
        }
    }

    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // "On click listener" for options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when(id) {
            R.id.action_settings -> { // Selected settings

            }
            R.id.action_about_us -> { // About us selected

            }
            R.id.action_rate_us -> { // About us selected

            }
        }

        return super.onOptionsItemSelected(item)
    }




//
//    /**
//     * A [FragmentPagerAdapter] that returns a fragment corresponding to
//     * one of the sections/tabs/pages.
//     */
//    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
//
//        override fun getItem(position: Int): Fragment {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1)
//        }
//
//        override fun getCount(): Int {
//            // Show 3 total pages.
//            return 3
//        }
//    }
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    class PlaceholderFragment : Fragment() {
//
//        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                                  savedInstanceState: Bundle?): View? {
//            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
//            rootView.section_label.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
//            return rootView
//        }
//
//        companion object {
//            /**
//             * The fragment argument representing the section number for this
//             * fragment.
//             */
//            private val ARG_SECTION_NUMBER = "section_number"
//
//            /**
//             * Returns a new instance of this fragment for the given section
//             * number.
//             */
//            fun newInstance(sectionNumber: Int): PlaceholderFragment {
//                val fragment = PlaceholderFragment()
//                val args = Bundle()
//                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
//                fragment.arguments = args
//                return fragment
//            }
//        }
//    }



    // Test dialog - ya know for testing stuff
    private
    fun testDialog(s : String) {
        // TEST DIALOG
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(s)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        // TEST DIALOG
    }

    // Print Place objects - again for testing
    private
    fun printPlace(place : Place) {
        testDialog("name: " + place.name +
                "\nrating: " + place.rating +
                "\nprice: " + place.price +
                "\ndesc: " + place.description)
    }

    // Return string of place info - again for testing
    private
    fun placeString(place : Place) : String {
        return "name: " + place.name +
                "\nrating: " + place.rating +
                "\nprice: " + place.price +
                "\ndesc: " + place.description
    }

}  // END CLASS MainActivity.kt