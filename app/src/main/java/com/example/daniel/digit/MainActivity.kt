package com.example.daniel.digit

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.StringReader
import java.net.URL


var style:String = "Random"
var price:Int = -1
var lat:Double = 0.0
var lng:Double = 0.0

class MainActivity : AppCompatActivity() {

    val styles = arrayOf("Random", "American", "Hispanic", "Italian", "Asian", "Breakfast", "Fast Food")
    val prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 101
    lateinit var placesList:List<JsonObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Setup spinners
        spinnerSetup()

        // Request location permission
        setupPermissions()
        makeRequest()

        //set on click listener for submitButton
        submitButton.setOnClickListener{

            // Run Async task for API call
            doAsync {
                try{
                    // Call API, store JsonObjects in placesList
                    placesList = streamJSON()
                    printJsonObject(placesList[0])
                } catch (e : java.lang.RuntimeException){
                    // Error
                    testDialog("Invalid Request")
                }

                uiThread {
                    // TODO: Check response is valid, if so store for next activity, else break and prompt user

                    //testDialog(result)
                }
            }

            // PASS DATA TO NEXT ACTIVITY

            // GO TO NEXT ACTIVITY
        }
    }

    // Creates URL for Place Search API call
    // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
    // &key         = api key
    // &location    = lat,lng
    // &radius      = 30 miles
    // &type        = restaurant
    // &keyword     = style
    // &maxprice    = price
    // &opennow     = true
    // &rank_by     = distance
    private
    fun searchUrlBuilder() : String {
        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + lat + "," + lng +
                "&type=food" +
                "&radius=10000" +
                "&opennow=true" +
                "&rank_by=distance"
        if(price == 0){ // User selected any price
            price = 5
        }
        url += "&maxprice=" + price

        if(style.compareTo("Random") == 1) { // User selected not random style
            url += "&keyword=" + style
        }

        url += "&key=" + getString(R.string.google_api_key) // Add API key
        return url
    }

    // Streams and parses JSON response from Places API
    private
    fun streamJSON() : ArrayList<JsonObject> {
        val klaxon = Klaxon()
        var result = arrayListOf<JsonObject>()
        JsonReader(StringReader(searchUrlBuilder())).use { reader -> reader.beginObject {
                while (reader.hasNext()) {
                    var name = reader.nextName()
                    if (name.equals("results")) { // Stores results array in return list
                        result = parseResultsArray(reader)
//                        result = parseResultsArray(reader.nextArray())
                    }
                    if(name.equals("status")) { // Checks if valid response
                        var status = reader.nextString()
                        if (!status.equals("OK")) {
                            throw RuntimeException("Invalid Request")
                        }
                    }
                }
            }
        }
        return result
    }

    // Parse results JSONArray from places API call
    private
    fun parseResultsArray(reader : JsonReader) : ArrayList<JsonObject> {
        var result = arrayListOf<JsonObject>()

        reader.beginArray {
            while(reader.hasNext()){
                result.add(reader.nextObject())
            }
        }

        return result
    }

//    private
//    fun parseResultsArray(list : List<Any>) : ArrayList<JsonObject> {
//        var result = arrayListOf<JsonObject>()
//
//        for ( n in list ) {
//            result.add(list[n])
//        }
//
//        return result
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

    // Prints JsonObjects - primarily for testing
    private
    fun printJsonObject(o : JsonObject) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(o as String)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Check permission response
    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // PERMISSION GRANTED - use sensors to get lat/lng
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                        // Got last known location. In some rare situations this can be null.
                        lat = location.latitude
                        lng = location.longitude
                    }
                } else {
                    // PERMISSION DENIED - prompt user for location
                    // TODO: IMPLEMENT GOOGLE PLACE SEARCH BOX FOR PEOPLE WHO DENY THE LOCATION PERMISSION
                    // CHECK PLACES AUTOCOMPLETE API
                    var res = 0
                    while(res == 0){
                        res = 1
                        // res = getUserInputLocation()
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    // Check if location permission is granted already
    private
    fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    // Make request for location permission
    private
    fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    // Setup spinners in MainActivity
    private
    fun spinnerSetup(){
        //Adapter for styleSpinner
        styleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, styles)
        //item selected listener for styleSpinner
        styleSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                style = styles[p2]
            }
        }

        //Adapter for priceSpinner
        priceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prices)
        //item selected listener for priceSpinner
        priceSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                price = p2
            }
        }
    }

//
//    private fun getUserInputLocation() : Int {
//        // WHILE LOOP CHECKING STATUS CODE
//
//        // PROMPT USER FOR LOCATION
//        alert {
//            title = "Where are you?"
//        }.show()
//
//        // CONVERT LOCATION TO LAT/LNG USING GOOGLE GEOCODING API
//        // https://maps.googleapis.com/maps/api/geocode/json?parameters
//
//        // PARSE JSON
//
//        // STORE IN lat/lng
//    }








//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//
//        if (id == R.id.action_settings) {
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
//
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

}
