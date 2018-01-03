package com.example.daniel.digit

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.daniel.digit.R.id.fab

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

var style:String = "Random"
var price:Int = -1

class MainActivity : AppCompatActivity() {

    //URL googleMaps = https://www.google.com/maps/search/mexican+food/@26.4228543,-80.1039212,14z/data=!4m3!2m2!5m1!1e0;

    var styleSpinner:Spinner = findViewById(R.id.styleSpinner)
    var priceSpinner:Spinner = findViewById(R.id.priceSpinner)

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aas = ArrayAdapter.createFromResource(this, R.array.style_spinner, android.R.layout.simple_spinner_item)
        val aap = ArrayAdapter.createFromResource(this, R.array.price_spinner, android.R.layout.simple_spinner_item)
//        val aas = ArrayAdapter(this, android.R.layout.simple_spinner_item, )
//        val aap = ArrayAdapter(this, android.R.layout.simple_spinner_item, "@string/price_spinner")

        // Set layout to use when the list of choices appear
        aas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        aap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set Adapter to Spinner
        styleSpinner.adapter = aas
        priceSpinner.adapter = aap
    }

//    //item selected listener for spinner
//    mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onNothingSelected(p0: AdapterView<*>?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//            Toast.makeText(this@MainActivity, myStrings[p2], LENGTH_LONG).show()
//        }
//
//    }

    //Style Spinner Listener
    private fun getStyleSpinner(spinner:Spinner):String {
        var style = "Random"
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                style = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return style
    }

    //Price Spinner Listener
    private fun getPriceSpinner(spinner:Spinner):Int {
        var price = -1
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                price = position - 1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return price
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

}
