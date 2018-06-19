package com.example.daniel.digit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beust.klaxon.JsonReader
import com.example.daniel.digit.R.id.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.*
import java.io.StringReader
import java.lang.RuntimeException
import java.lang.Thread.sleep
import java.net.URL

const val EXTRA_PLACES_LIST = "com.example.daniel.digit.PLACESLIST"
const val EXTRA_LIST_SIZE = "com.example.daniel.digit.PLACESLISTSIZE"
var style:String = "Random"
var price:Int = -1
var lat:Double = 0.0
var lng:Double = 0.0

class MainActivity : AppCompatActivity() {

    val styles = arrayOf("Random", "American", "Hispanic", "Italian", "Asian", "Breakfast", "Fast Food")
    val prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private val locationRequestCode = 101
    var placesList = ArrayList<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar)



        //test places ******************************************************************************************************
        var arr1 = DoubleArray(2)
        arr1[0] = 26.3523517
        arr1[1] = -80.1568702
        var place1 = Place("Sweet Tomatoes", "e5922636c1c678cec92268ce9e03907613f258e6", "Restaurant",
                "CmRaAAAAoawTX5603PlBx7KE3H0OhaD6FkyHRyeqwj_MopXLvtYirZWrvvqrYzpbk2sPzhnEkq-aiXKeozAMshBbPgZSuHpMLTcAtB8aeynfpZ__-o2lJPMrPI-VjYgkySWLwH7dEhAbX5XTWMN6So_5ABc80CRiGhTR3N0t8tOjcAUwpmHRbt77gf9h9w",
                1, 4, arr1)
        var arr2 = DoubleArray(2)
        arr2[0] = 26.362137
        arr2[1] = -80.15299999999999
        var place2 = Place("Bonefish Grill", "3411a36e4d6b0c1c87adab1cd73c3ae0314cebe1", "bar",
                "CmRaAAAAxTcFWTCAoMk0OwYncPFV6J6imuUGTA3B-uX2twxcoFw6Dv9SRpRtZNqVqIW73BqRzwzy9D9jCJxAl0CT-j34kBUB7WzrkVz3s1BuHMZYMt6hzGJycFPe57qgsPLM8MFwEhBfLdBuMYPx6FVjlc9X1yKlGhTnHmkyfRPVRiaTb8RcRSTS-ybBlQ",
                2, 4, arr2)
        var arr3 = DoubleArray(2)
        arr3[0] = 26.4618978
        arr3[1] = -80.07089839999999
        var place3 = Place("The Office Delray", "a53bd95ae1f5779b7a415ec1cddfb6af928b0189", "bar",
                "CmRaAAAAepicIBIE9K8v5awRAUFBgF_FCVGA7j4wJOjvABr2GhgUjxpb361h9MST6OcjGxQ_yImMq2O2QcvWv21_dbuMhPHMLXbZoWzpIEQPG2h8GaNcO_qyVuCGO0j7Z6BNE2TEEhBpbGXT2rrM3Bm4EMbkA70UGhSD5HPWwLdrAZ6qoiMUMdPtX7ilUw",
                2, 4, arr3)
        var arr4 = DoubleArray(2)
        arr3[0] = 26.365652
        arr3[1] = -80.12607299999999
        var place4 = Place("Hooters", "fd78a0c66e03b22525d004c666be633869c827cc", "bar",
                "CmRaAAAAmHecV-otAdP1vBPhCFs3BvEFvLFZHwZ82vvxuD7-wt_6tfSED7AyxjdE5ivAAAGKkjpWEytVSDsHgO7W86ZhPIWBuwfHtd0tKyeu2Dzc40aAmKM6x-6gE1wcRc4CdktPEhBbIYPCE09h5U0vqiXJvc7tGhRmAgR83YO54Bgg7sa9scOeHm0S2g",
                2, 3, arr4)

        placesList.add(place1)
        placesList.add(place2)
        placesList.add(place3)
        placesList.add(place4)
        // *****************************************************************************************************************



        // Setup spinners
        spinnerSetup()

        // Request location permission
        setupPermissions()

        //set on click listener for submitButton
        submitButton.setOnClickListener{
//            doAsync {
//                try {
//                    // Call API, store Place objects in placesList
//                    testDialog("streamJSON call")
//                    placesList = streamJSON()
//                } catch (e: java.lang.RuntimeException) {
//                    // Error parsing JSON
//                    testDialog("Invalid Request")
//                }
//
//                uiThread {
//                    // Go to ResultsActivity, pass placesList
//                    val intent = Intent(this@MainActivity, ResultsActivity::class.java).apply {
//                        putExtra(EXTRA_PLACES_LIST, placesList)
//                        putExtra(EXTRA_LIST_SIZE, placesList.size)
//                    }
//                    startActivity(intent)
//                }
//            }


            val intent = Intent(this@MainActivity, ResultsActivity::class.java).apply {
//                putExtra(EXTRA_PLACES_LIST, placesList)
//                putExtra(EXTRA_LIST_SIZE, placesList.size)
            }
            startActivity(intent)
        }

        //set on click listener for submitButton
        feelingLuckyButton.setOnClickListener {
//            doAsync {
//                try {
//                    // Call API, store Place objects in placesList
//                    placesList = streamJSON()
//                } catch (e: java.lang.RuntimeException) {
//                    // Error parsing JSON
//                    testDialog("Invalid Request")
//                }
//
//                uiThread {
//                    // Open Google Maps link
//                    placesList[0].openWebPage(this)
//                }
//            }

            // Alerts user of location selected
            alert("You have selected " + placesList[0].name + " click OK to open Google Maps") {
                title = "I'm Feeling Lucky!"
                yesButton { placesList[0].openWebPage(this@MainActivity) }
                noButton { }
            }.show()
        }
    }

    // Creates URL for Place Search API call
    private
    fun searchUrlBuilder() : String {
        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
        // &key         = api key
        // &location    = lat,lng
        // &radius      = 30 miles
        // &type        = restaurant
        // &keyword     = style
        // &maxprice    = price
        // &opennow     = true
        // &rank_by     = distance

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
    fun streamJSON() : ArrayList<Place> {
//        var result = arrayListOf<JsonObject>()
//        JsonReader(StringReader(searchUrlBuilder())).use { reader -> reader.beginObject() {
//                while (reader.hasNext()) {
//                    var name = reader.nextName()
//                    if (name.equals("results")) { // Stores results array in return list
//                        result = parseResultsArray(reader)
////                        result = parseResultsArray(reader.nextArray())
//                    }
//                    if(name.equals("status")) { // Checks if valid response
//                        var status = reader.nextString()
//                        if (!status.equals("OK")) {
//                            throw RuntimeException("Invalid Request")
//                        }
//                    }
//                }
//            }
//        return result
        var res = ArrayList<Place>()
        JsonReader(StringReader(URL(searchUrlBuilder()).readText())).use {
            reader -> reader.beginObject {
                res = readStream(reader)
            }
        }
        return res
    }

    // Reads stream, sends to corresponding functions for parsing
    private
    fun readStream(reader : JsonReader) : ArrayList<Place> {
        var places = ArrayList<Place>()
        //reader.beginObject {
            while (reader.hasNext()) {
                var name = reader.nextName()
                testDialog(name)
                testDialog(name)
                if(name.equals("html_attributions")){
                    name = reader.nextName()
                }
                if(name.equals("results")) {
                    places.add(readJsonObject(reader))
                }
                if(name.equals("status")) {
                    var status = reader.nextString()
                    if(!status.equals("OK")){
                        throw RuntimeException("Invalid Request")
                    }
                }
            }
        //}

        return places
    }

    // Reads place JSONObjects and creates corresponding Place objects
    private
    fun readJsonObject(reader : JsonReader) : Place {
        var placeName = "Not Available"
        var placeID = ""
        var description = "N/A"
        var photoRef = "DEFAULT"
        var price = 0
        var rating = 0
        var locationArray = DoubleArray(2)

        reader.beginArray {
            while(reader.hasNext()){
                var name = reader.nextName()
                testDialog(name)
                when(name) {
                    "geometry" -> locationArray = getLocation(reader)
                    "name" -> placeName = reader.nextString()
                    "photos" -> photoRef = getPhotoRef(reader)
                    "place_id" -> placeID = reader.nextString()
                    "price_level" -> price = reader.nextInt()
                    "rating" -> rating = reader.nextString().toInt()
                    "types" -> description = getDescription(reader)
                }

//                if(name.equals("geometry")) { // location
//                    var locationArray : DoubleArray = getLocation(reader)
//                    lat = locationArray[0]
//                    lng = locationArray[1]
//                } else if (name.equals("name")) { // name
//                    placeName = reader.nextString()
//                } else if (name.equals("photos")) { // photo ref
//                    photoRef = getPhotoRef(reader)
//                } else if (name.equals("place_id")) { // place id
//                    placeID = reader.nextString()
//                } else if (name.equals("price_level")) { // price
//                    price = reader.nextInt()
//                } else if (name.equals("rating")) { // rating
//                    rating = reader.nextString().toInt()
//                } else if (name.equals("types")) { // description
//                    description = getDescription(reader)
//                }
            }
        }
        var place = Place(placeName, placeID, description, photoRef, price, rating, locationArray)
        return place
    }

    // Gets location array out of JSON response
    private
    fun getLocation(reader : JsonReader) : DoubleArray {
        var resArray = DoubleArray(2)
        reader.beginObject {
            while(reader.hasNext()){
                var name = reader.nextName()
                if(name.equals("location")){
                    reader.beginObject {
                        resArray[0] = reader.nextString().toDouble()
                        resArray[1] = reader.nextString().toDouble()
                    }
                }
            }
        }
        return resArray
    }

    // Gets photo reference out of JSON response
    private
    fun getPhotoRef(reader : JsonReader) : String {
        var photoRef = "No Photo"

        reader.beginArray {
            reader.beginObject {
                while(reader.hasNext()){
                    var name = reader.nextName()
                    if(name.equals("photo_reference")){
                        photoRef = reader.nextString()
                    }
                }
            }
        }

        return photoRef
    }

    // Gets description out of JSON response
    private
    fun getDescription(reader : JsonReader) : String {
        var desc = "No description available"

        reader.beginArray {
            desc = reader.nextString()
        }

        return desc
    }

//    // Parse results JSONArray from places API call
//    private
//    fun parseResultsArray(reader : JsonReader) : ArrayList<JsonObject> {
//        var result = arrayListOf<JsonObject>()
//
//        reader.beginArray {
//            while(reader.hasNext()){
//                result.add(reader.nextObject())
//            }
//        }
//
//        return result
//    }
//
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationRequestCode)
    }

    // Check permission response
    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            locationRequestCode -> {
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

}  // END CLASS MainActivity.kt
