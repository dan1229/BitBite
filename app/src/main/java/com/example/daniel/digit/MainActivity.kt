package com.example.daniel.digit

import android.Manifest
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.daniel.digit.R.id.fab
import com.example.daniel.digit.R.layout.activity_main

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.jetbrains.anko.alert
import android.content.pm.ApplicationInfo
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.jetbrains.anko.toast


var style:String = "Random"
var price:Int = -1
var lat:Double = 0.0
var lng:Double = 0.0

class MainActivity : AppCompatActivity() {

    //URL googleMaps = https://www.google.com/maps/search/mexican+food/@26.4228543,-80.1039212,14z/data=!4m3!2m2!5m1!1e0;

    var styles = arrayOf("Random", "American", "Hispanic", "Italian", "Asian", "Breakfast", "Fast Food")
    var prices = arrayOf("Any Price", "$", "$$", "$$$", "$$$$", "$$$$$")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Get API Key
        val api_key = "@meta-data/value"

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

        //set on click listener for submitButton
        submitButton.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // LOCATION PERMISSION NOT GRANTED - GET USER LOCATION
                var res = 0
                while(res == 0){
                    res = 1
                    // res = getUserInputLocation()
                }

                // DIALOG BOX FOR TESTING
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Lat: " + lat + "\nLng: " + lng)
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            else{
                // GET LOCATION FROM DEVICE
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location ->
                            // Got last known location. In some rare situations this can be null.
                            lat = location.getLatitude()
                            lng = location.getLongitude()

                            // DIALOG BOX FOR TESTING
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setMessage("Lat: " + lat + "\nLng: " + lng)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
            }

            // MAKE API CALL (lat, lng, style, price)
            // https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters
            // &key
            // &radius
            // &keyword
            // &minprice and &maxprice
            // &opennow
            // &rankbys



            // PASS DATA TO NEXT ACTIVITY

            // GO TO NEXT ACTIVITY
        }
    }
//
//    fun getUserInputLocation() : Int {
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
