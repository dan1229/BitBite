package com.example.daniel.digit

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : AppCompatActivity() {

    var places = Any()
    var listSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        toolbar.setTitle("Results")
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get places ArrayList and size from MainActivity
        val bundle = intent.extras
        //places = bundle.get("EXTRA_PLACES_LIST")
        //listSize = bundle.getInt("EXTRA_LIST_SIZE")


        // ArrayList index
        var index = 0

        // Update cards in activity_main.xml
        updateCard(1, index)
        index = next_index(index)
        updateCard(2, index)
        index = next_index(index)
        updateCard(3, index)
        index = next_index(index)

        displayNext3.setOnClickListener{
            if (index > listSize) { // Index in bounds, update cards
                for (i in 1..3) {
                    updateCard(i, index)
                    index = next_index(index)
                }
            }
            else { // Index out of bounds, display error message
                testDialog("Out of options. Please go back or search again.")
            }
        }
    }

    // Calls update function for each segment of card
    fun updateCard(card : Int, index : Int) {
        if(index < listSize) { // within bounds of list
            updateName(card, index)
            updatePrice(card, index)
            updatePhoto(card, index)
            updateRating(card, index)
            updateDescription(card, index)
        }
        else{ // greater than size of list
            updateName(card, -1)
            updatePrice(card, -1)
            updatePhoto(card, -1)
            updateRating(card, -1)
            updateDescription(card, -1)
        }
    }

    // Updates name on the card
    fun updateName(card : Int, index : Int) {

    }

    // Updates price on the card
    fun updatePrice(card : Int, index : Int) {

    }

    // Updates phto on the card
    fun updatePhoto(card : Int, index : Int) {

    }

    // Updates rating on the card
    fun updateRating(card : Int, index : Int) {

    }

    // Updates description on the card
    fun updateDescription(card : Int, index : Int) {

    }

    // "Iterator" function for index
    fun next_index(n : Int) : Int{
        var i = n
        return ++i
    }

    // Test dialog - ya know for testing stuff
    private
    fun testDialog(s : String) {
        // TEST DIALOG
        val builder = AlertDialog.Builder(this@ResultsActivity)
        builder.setMessage(s)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        // TEST DIALOG
    }
}
