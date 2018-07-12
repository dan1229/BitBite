package com.example.daniel.bitbite

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_more_info.*

class MoreInfoActivity : AppCompatActivity(), TopCard.OnFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_ExplodeTransition)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        setSupportActionBar(toolbar_moreinfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



    }


    /**====================================================================================================**/
    /** Updater Methods **/

    // responseUpdates()
    // Updates fields from detailsResponse object (Place Details)
    private
    fun responseUpdates(response : DetailsResponse?) {

    }




    /**====================================================================================================**/
    /** Fragment Methods **/

    // onFragmentInteraction()
    // Mandatory implementation for interface
    override fun onFragmentInteraction(uri: Uri) {
        //
    }

} // END CLASS MoreInfoActivity.kt
