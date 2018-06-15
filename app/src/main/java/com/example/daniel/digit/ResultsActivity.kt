package com.example.daniel.digit

import android.os.Bundle
import android.support.design.widget.Snackbar
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

        // Get places ArrayList from MainActivity
        val bundle = intent.extras
        places = bundle.get("EXTRA_PLACES_LIST")

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
            for (i in 1..3){
                updateCard( i, index)
                index = next_index(index)
            }
        }
    }

    // Calls update function for each segment of card
    fun updateCard(card : Int, index : Int) {
        updateName(card, index)
        updatePrice(card, index)
        updatePhoto(card, index)
        updateRating(card, index)
        updateDescription(card, index)
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
    fun next_index(i : Int) : Int{
        return ((++i) % places.size())
    }

}
