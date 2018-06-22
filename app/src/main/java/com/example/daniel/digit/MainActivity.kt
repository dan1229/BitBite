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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beust.klaxon.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.lang.RuntimeException
import java.net.URL
import java.util.*

// Constants
const val EXTRA_PLACES_LIST = "com.example.daniel.digit.PLACESLIST"

class MainActivity : AppCompatActivity() {

    // Spinner options
    val styles = arrayOf("Random", "Hispanic", "Italian", "Asian", "Indian", "Breakfast", "Fast Food")
    val prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    // Variables
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private val locationRequestCode = 101
    var changed = false
    var placesList = ArrayList<Place>()
    var style:String = "Random"
    var price:Int = -1
    var lat:Double = 0.0
    var lng:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar)

//

        // Setup spinners
        spinnerSetup()

        // Request location permission
        setupPermissions()

        //set on click listener for submitButton
        submitButton.setOnClickListener{
            try{
                asyncCall(1)
            } catch (e : RuntimeException) {
                errorAlert(e)
            }
        }

        //set on click listener for I'm feeling lucky button
        feelingLuckyButton.setOnClickListener {
            if(placesList.isEmpty()) {
                try {
                    asyncCall(2)
                } catch (e : RuntimeException){
                    errorAlert(e)
                }
            }
            else{
                feelingLuckyAlert()
            }
        }
    }

    // Creates URL for Place Search API call
    private
    fun searchUrlBuilder() : String {
        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters

        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + lat + "," + lng +
                "&type=restaurant" +
                "&radius=250000" +
                "&opennow=true" +
                "&keyrword=" + style +
                "&rank_by=distance"
        if(price == 0){ // User selected any price
            price = 5
        }
        url += "&maxprice=" + price
        url += "&key=" + getString(R.string.google_api_key) // Add API key
        return url
    }

    // Calls Places API in Async thread, returns ArrayList<Place> for placesList
    // @Param
    // 1 = submit button
    // 2 = feeling lucky button
    private
    fun asyncCall(n : Int) {
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
                else if (n == 2) { // Feeling lucky button - alert
                    feelingLuckyAlert()
                }
            }
        }
    }

    // Creates intent for ResultsActivity, includes placesList in bundle, starts Activity
    private
    fun goToResults() {
        val intent = Intent(this@MainActivity, ResultsActivity::class.java)
        var bundle = Bundle()
        bundle.putParcelableArrayList(EXTRA_PLACES_LIST, placesList)
        intent.putExtra("myBundle",bundle)
        startActivity(intent)
    }

    // Alerts user of location selected and opens Google Maps link
    private
    fun feelingLuckyAlert() {
        alert("You have selected " + placesList[0].name + " click OK to open Google Maps") {
            title = "I'm Feeling Lucky"
            yesButton { placesList[0].openWebPage(this@MainActivity) }
            noButton { }
        }.show()
    }

    private
    fun errorAlert(e : RuntimeException) {
        alert(e.toString() + ". Please try again.") {
            title = "Uh Oh!"
        }.show()
    }

    class Response(val next_page_token:String = "EMPTY", val results:List<Results>, val status:String)

    class Results(val geometry:Geometry, val name:String="Not Available", val photos:List<Photos>, val place_id:String="",
                  val price_level:Int=0, val rating:Double=0.0, val types:Array<String>)

    class Geometry(val location:LocationObj)

    class LocationObj(val lat:Double, val lng:Double)

    class Photos(val photo_reference:String="DEFAULT")

    // Streams and parses JSON response from Places API
    private
    fun streamJSON() : ArrayList<Place> {
        var res = ArrayList<Place>()
        Log.d("STREAM", searchUrlBuilder())
        Log.d("STREAM", URL(searchUrlBuilder()).readText())
        var response = Klaxon().parse<Response>(URL(searchUrlBuilder()).readText())
        if(response!!.status != "OK"){
            if((response!!.status == "ZERO_RESULTS") || (response!!.results.isEmpty())) {
                Log.d("STREAM", "zero-res")
                throw RuntimeException("No Results")
            }
            else {
                Log.d("STREAM", "invalid")
                throw RuntimeException("Invalid Request")
            }
        }
        Log.d("STREAM", response.results.size.toString())

        for(i in 0 until (response.results.size)){
            var place = convertToPlace(response.results[i])
            res.add(place)
        }

        return res
    }

    private
    fun convertToPlace(results : Results) :  Place {
        val name = results.name
        val placeID = results.place_id
        val description = results.types[0]
        val photoRef = results.photos[0].photo_reference
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
        }
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
                var i = p2
                if(i == 0)
                   i = (0..styles.size).random()
                style = styles[i]
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

    // Random Number Generator
    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start

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


    //test places ******************************************************************************************************
//        var arr1 = DoubleArray(2)
//        arr1[0] = 26.3523517
//        arr1[1] = -80.1568702
//        var place1 = Place("Sweet Tomatoes", "e5922636c1c678cec92268ce9e03907613f258e6", "Restaurant",
//                "CmRaAAAAoawTX5603PlBx7KE3H0OhaD6FkyHRyeqwj_MopXLvtYirZWrvvqrYzpbk2sPzhnEkq-aiXKeozAMshBbPgZSuHpMLTcAtB8aeynfpZ__-o2lJPMrPI-VjYgkySWLwH7dEhAbX5XTWMN6So_5ABc80CRiGhTR3N0t8tOjcAUwpmHRbt77gf9h9w",
//                1, 4, arr1)
//        var arr2 = DoubleArray(2)
//        arr2[0] = 26.362137
//        arr2[1] = -80.15299999999999
//        var place2 = Place("Bonefish Grill", "3411a36e4d6b0c1c87adab1cd73c3ae0314cebe1", "bar",
//                "CmRaAAAAxTcFWTCAoMk0OwYncPFV6J6imuUGTA3B-uX2twxcoFw6Dv9SRpRtZNqVqIW73BqRzwzy9D9jCJxAl0CT-j34kBUB7WzrkVz3s1BuHMZYMt6hzGJycFPe57qgsPLM8MFwEhBfLdBuMYPx6FVjlc9X1yKlGhTnHmkyfRPVRiaTb8RcRSTS-ybBlQ",
//                2, 4, arr2)
//        var arr3 = DoubleArray(2)
//        arr3[0] = 26.4618978
//        arr3[1] = -80.07089839999999
//        var place3 = Place("The Office Delray", "a53bd95ae1f5779b7a415ec1cddfb6af928b0189", "bar",
//                "CmRaAAAAepicIBIE9K8v5awRAUFBgF_FCVGA7j4wJOjvABr2GhgUjxpb361h9MST6OcjGxQ_yImMq2O2QcvWv21_dbuMhPHMLXbZoWzpIEQPG2h8GaNcO_qyVuCGO0j7Z6BNE2TEEhBpbGXT2rrM3Bm4EMbkA70UGhSD5HPWwLdrAZ6qoiMUMdPtX7ilUw",
//                2, 4, arr3)
//        var arr4 = DoubleArray(2)
//        arr3[0] = 26.365652
//        arr3[1] = -80.12607299999999
//        var place4 = Place("Hooters", "fd78a0c66e03b22525d004c666be633869c827cc", "bar",
//                "CmRaAAAAmHecV-otAdP1vBPhCFs3BvEFvLFZHwZ82vvxuD7-wt_6tfSED7AyxjdE5ivAAAGKkjpWEytVSDsHgO7W86ZhPIWBuwfHtd0tKyeu2Dzc40aAmKM6x-6gE1wcRc4CdktPEhBbIYPCE09h5U0vqiXJvc7tGhRmAgR83YO54Bgg7sa9scOeHm0S2g",
//                2, 3, arr4)
//
//        placesList.add(place1)
//        placesList.add(place2)
//        placesList.add(place3)
//        placesList.add(place4)
//        // *****************************************************************************************************************


}  // END CLASS MainActivity.kt